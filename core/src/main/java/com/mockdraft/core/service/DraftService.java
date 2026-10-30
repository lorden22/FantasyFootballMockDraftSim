package com.mockdraft.core.service;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.random.RandomGenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mockdraft.core.db.Database;
import com.mockdraft.core.db.DuplicateKeyException;
import com.mockdraft.core.draft.DraftState;
import com.mockdraft.core.draft.DraftStrategy;
import com.mockdraft.core.draft.RosterBuilder;
import com.mockdraft.core.draft.WeightedPicker;
import com.mockdraft.core.model.Draft;
import com.mockdraft.core.model.DraftTeam;
import com.mockdraft.core.model.Pick;
import com.mockdraft.core.model.Player;
import com.mockdraft.core.model.Roster;
import com.mockdraft.core.repo.DraftRepository;
import com.mockdraft.core.repo.PlayerRepository;
import com.mockdraft.core.repo.UserRepository;

/**
 * Everything a user can do with a draft. Both the web API and the console app call these methods, so
 * drafts behave identically (and are stored identically) whichever client created them.
 *
 * <p>Every method takes the acting username and only ever touches that user's drafts. Mutations lock
 * the draft row, so concurrent requests for the same draft are serialized.
 */
public final class DraftService {

    private static final Logger log = LoggerFactory.getLogger(DraftService.class);

    public static final int ROUNDS = 15;
    /** Upper bound on saved drafts per account, to keep storage per user bounded. */
    public static final int MAX_SAVED_DRAFTS = 250;

    private final Database db;
    private final DraftRepository drafts;
    private final PlayerRepository players;
    private final UserRepository users;
    private final DraftStrategy strategy;
    private final RandomGenerator random;
    private final Clock clock;

    public DraftService(Database db, PlayerRepository players, RandomGenerator random, Clock clock) {
        this.db = db;
        this.players = players;
        this.drafts = new DraftRepository(db, players);
        this.users = new UserRepository(db);
        this.random = random;
        this.strategy = new DraftStrategy(new WeightedPicker(random));
        this.clock = clock;
    }

    public List<Draft> list(String username) {
        return drafts.list(username);
    }

    public DraftState get(String username, long draftId) {
        return drafts.loadState(find(username, draftId));
    }

    /** The user's in-progress draft, if any. */
    public java.util.Optional<DraftState> current(String username) {
        return drafts.findInProgress(username).map(drafts::loadState);
    }

    /**
     * Creates a draft. CPU teams are named "CPU Team N".
     *
     * @param draftPosition 1-based slot, or null for a random slot
     */
    public DraftState create(String username, String teamName, int numTeams, Integer draftPosition) {
        String name = Validators.teamName(teamName);
        Validators.numTeams(numTeams);
        Validators.draftPosition(draftPosition, numTeams);
        int userSlot = draftPosition != null ? draftPosition : random.nextInt(numTeams) + 1;

        long id = db.inTransaction(() -> {
            users.lock(username); // serialize creates per user so the one-in-progress rule holds
            if (drafts.findInProgress(username).isPresent()) {
                throw new ConflictException("You already have a draft in progress. Resume or delete it first.");
            }
            if (drafts.count(username) >= MAX_SAVED_DRAFTS) {
                throw new ConflictException("You have reached the limit of " + MAX_SAVED_DRAFTS
                        + " saved drafts. Delete some old drafts to start another.");
            }
            long draftId = drafts.insertDraft(username, name, numTeams, ROUNDS, userSlot, clock.instant());
            List<DraftTeam> teams = new ArrayList<>();
            for (int slot = 1; slot <= numTeams; slot++) {
                teams.add(slot == userSlot ? new DraftTeam(slot, name, true) : new DraftTeam(slot, "CPU Team " + slot, false));
            }
            drafts.insertTeams(draftId, teams);
            return draftId;
        });
        log.info("User {} created draft {} ({} teams, slot {})", username, id, numTeams, userSlot);
        return get(username, id);
    }

    /** All undrafted players, best rank first. */
    public List<Player> available(String username, long draftId) {
        return available(get(username, draftId));
    }

    public List<Player> available(DraftState state) {
        Set<Integer> taken = state.draftedRanks();
        return players.all().stream().filter(p -> !taken.contains(p.rank())).toList();
    }

    /** The user drafts a specific player. Fails if it is not their turn or the player is gone. */
    public DraftState makeUserPick(String username, long draftId, int playerRank) {
        return mutate(username, draftId, state -> {
            requireUserTurn(state);
            if (!players.exists(playerRank)) {
                throw new ValidationException("playerRank", "Unknown player.");
            }
            if (state.draftedRanks().contains(playerRank)) {
                throw new ConflictException("That player has already been drafted.");
            }
            record(state, players.get(playerRank));
        });
    }

    /** Makes the user's pick using the same logic CPU teams use (best fit, no randomness). */
    public DraftState autoPick(String username, long draftId) {
        return mutate(username, draftId, state -> {
            requireUserTurn(state);
            DraftState.Turn turn = state.currentTurn();
            record(state, strategy.choose(available(state), state.picksForSlot(turn.slot()), turn.round(),
                    state.draft().rounds(), true));
        });
    }

    /** CPU teams pick until it is the user's turn or the draft ends. */
    public DraftState simulateToUserTurn(String username, long draftId) {
        return mutate(username, draftId, start -> {
            DraftState state = start;
            while (!state.isComplete() && !state.isUserTurn()) {
                DraftState.Turn turn = state.currentTurn();
                Player choice = strategy.choose(available(state), state.picksForSlot(turn.slot()), turn.round(),
                        state.draft().rounds(), false);
                state = record(state, choice);
            }
        });
    }

    public Roster roster(String username, long draftId, int slot) {
        DraftState state = get(username, draftId);
        if (slot < 1 || slot > state.draft().numTeams()) {
            throw new NotFoundException("No such team in this draft.");
        }
        return RosterBuilder.build(state, slot);
    }

    public void delete(String username, long draftId) {
        db.inTransaction(() -> {
            drafts.findForUpdate(username, draftId).orElseThrow(() -> new NotFoundException("Draft not found."));
            drafts.delete(draftId);
        });
        log.info("User {} deleted draft {}", username, draftId);
    }

    private interface Mutation {
        void apply(DraftState state);
    }

    private DraftState mutate(String username, long draftId, Mutation mutation) {
        db.inTransaction(() -> {
            Draft draft = drafts.findForUpdate(username, draftId)
                    .orElseThrow(() -> new NotFoundException("Draft not found."));
            DraftState state = drafts.loadState(draft);
            if (state.isComplete()) {
                throw new ConflictException("This draft is already complete.");
            }
            mutation.apply(state);
            if (drafts.find(username, draftId).orElseThrow().picksMade() >= draft.totalPicks()) {
                drafts.markComplete(draftId, clock.instant());
                log.info("Draft {} complete", draftId);
            }
        });
        return get(username, draftId);
    }

    private static void requireUserTurn(DraftState state) {
        if (!state.isUserTurn()) {
            throw new ConflictException("It is not your turn to pick.");
        }
    }

    /** Persists the pick on the clock and returns the state with that pick appended. */
    private DraftState record(DraftState state, Player player) {
        DraftState.Turn turn = state.currentTurn();
        try {
            drafts.insertPick(state.draft().id(), turn.overall(), turn.slot(), player.rank(), clock.instant());
        } catch (DuplicateKeyException e) {
            throw new ConflictException("The draft changed while you were picking. Refresh and try again.");
        }
        List<Pick> picks = new ArrayList<>(state.picks());
        picks.add(new Pick(turn.overall(), turn.round(), turn.pickInRound(), turn.slot(), turn.teamName(), player));
        return new DraftState(state.draft(), state.teams(), picks);
    }

    private Draft find(String username, long draftId) {
        return drafts.find(username, draftId).orElseThrow(() -> new NotFoundException("Draft not found."));
    }
}
