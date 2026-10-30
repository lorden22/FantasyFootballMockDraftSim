package com.mockdraft.console;

import java.util.List;
import java.util.Locale;

import com.mockdraft.core.draft.DraftState;
import com.mockdraft.core.model.Pick;
import com.mockdraft.core.model.Player;
import com.mockdraft.core.model.Position;
import com.mockdraft.core.service.ConflictException;
import com.mockdraft.core.service.DraftService;
import com.mockdraft.core.service.ValidationException;

/**
 * Interactive draft loop. Every pick is saved through {@link DraftService} as it happens, so quitting at any
 * point leaves a draft that can be resumed later from either the console or the web app.
 */
final class DraftRoom {

    private static final int PAGE = 10;

    private final Terminal t;
    private final Views views;
    private final DraftService drafts;
    private final String username;
    private final long draftId;

    private Position filter;
    private String search;
    private int page;

    DraftRoom(Terminal t, DraftService drafts, String username, long draftId) {
        this.t = t;
        this.views = new Views(t);
        this.drafts = drafts;
        this.username = username;
        this.draftId = draftId;
    }

    void run() {
        DraftState state = drafts.get(username, draftId);
        t.heading("Draft #" + draftId + " · " + state.draft().teamName() + " · " + state.draft().numTeams()
                + " teams · you pick from slot " + state.draft().userSlot());
        t.info("Type h for commands. Your picks save automatically — q leaves and you can resume any time.");

        while (!state.isComplete()) {
            if (!state.isUserTurn()) {
                state = simulate(state);
                continue;
            }
            showClock(state);
            showAvailable(state);
            String input = t.prompt("Pick # or command: ");
            if (input.isEmpty()) {
                continue;
            }
            try {
                DraftState next = handle(input, state);
                if (next == null) {
                    return; // q
                }
                state = next;
            } catch (ValidationException | ConflictException e) {
                t.error(e.getMessage());
            }
        }
        finish(state);
    }

    private DraftState simulate(DraftState before) {
        int already = before.picks().size();
        t.println();
        t.info("Simulating CPU picks…");
        DraftState after = drafts.simulateToUserTurn(username, draftId);
        for (Pick p : after.picks().subList(already, after.picks().size())) {
            views.pickLine(p, false);
        }
        return after;
    }

    private void showClock(DraftState state) {
        DraftState.Turn turn = state.currentTurn();
        t.println();
        t.println(t.inverse(t.bold(String.format(" ON THE CLOCK  Round %d · Pick %d.%02d · #%d overall ",
                turn.round(), turn.round(), turn.pickInRound(), turn.overall()))));
        var counts = new java.util.EnumMap<Position, Integer>(Position.class);
        for (Pick p : state.picksForSlot(state.draft().userSlot())) {
            counts.merge(p.player().position(), 1, Integer::sum);
        }
        StringBuilder needs = new StringBuilder("  Your roster: ");
        for (Position p : Position.values()) {
            needs.append(p.name()).append(' ').append(counts.getOrDefault(p, 0)).append("  ");
        }
        t.println(t.dim(needs.toString()));
    }

    private List<Player> filtered(DraftState state) {
        String q = search == null ? null : search.toLowerCase(Locale.ROOT);
        return drafts.available(state).stream()
                .filter(p -> filter == null || p.position() == filter)
                .filter(p -> q == null || p.name().toLowerCase(Locale.ROOT).contains(q))
                .toList();
    }

    private void showAvailable(DraftState state) {
        List<Player> list = filtered(state);
        if (list.isEmpty()) {
            t.info("  No players match" + (filter != null ? " position " + filter : "") + (search != null ? " \"" + search + "\"" : "")
                    + ". Type f to clear filters.");
            return;
        }
        int from = Math.min(page * PAGE, Math.max(0, list.size() - 1) / PAGE * PAGE);
        page = from / PAGE;
        String label = "Best available" + (filter != null ? " · " + filter : "") + (search != null ? " · \"" + search + "\"" : "");
        t.println(t.bold("  " + label) + t.dim(String.format("  (%d-%d of %d)", from + 1, Math.min(from + PAGE, list.size()), list.size())));
        views.players(list.subList(from, Math.min(from + PAGE, list.size())), from, state.draft().numTeams());
    }

    /** Returns the new state, or null to leave the draft room. */
    private DraftState handle(String input, DraftState state) {
        String[] parts = input.split("\\s+", 2);
        String cmd = parts[0].toLowerCase(Locale.ROOT);
        String arg = parts.length > 1 ? parts[1].strip() : "";

        if (cmd.matches("\\d+")) {
            int index = Integer.parseInt(cmd) - 1;
            List<Player> list = filtered(state);
            if (index < 0 || index >= list.size()) {
                t.error("No player #" + cmd + " in the current list.");
                return state;
            }
            Player choice = list.get(index);
            DraftState next = drafts.makeUserPick(username, draftId, choice.rank());
            t.success("You drafted " + choice.name() + " (" + choice.position() + ")");
            resetBrowse();
            return next;
        }
        switch (cmd) {
            case "a" -> {
                DraftState next = drafts.autoPick(username, draftId);
                Pick p = next.picks().get(next.picks().size() - 1);
                t.success("Auto-picked " + p.player().name() + " (" + p.player().position() + ")");
                resetBrowse();
                return next;
            }
            case "f" -> {
                if (arg.isEmpty()) {
                    filter = null;
                    search = null;
                } else {
                    try {
                        filter = Position.parse(arg);
                    } catch (IllegalArgumentException e) {
                        t.error("Positions: QB RB WR TE K DST");
                    }
                }
                page = 0;
            }
            case "s" -> {
                search = arg.isEmpty() ? null : arg;
                page = 0;
            }
            case "m", "n" -> page++;
            case "p" -> page = Math.max(0, page - 1);
            case "r" -> {
                int slot = state.draft().userSlot();
                if (!arg.isEmpty()) {
                    try {
                        slot = Integer.parseInt(arg);
                    } catch (NumberFormatException e) {
                        t.error("Usage: r [team slot]");
                        return state;
                    }
                }
                views.roster(drafts.roster(username, draftId, slot), state.draft().numTeams());
                t.prompt(t.dim("Enter to continue "));
            }
            case "t" -> {
                state.teams().forEach(team -> t.println(String.format("  %2d  %s%s", team.slot(), team.name(), team.isUser() ? "  (you)" : "")));
            }
            case "b" -> {
                views.board(state);
                t.prompt(t.dim("Enter to continue "));
            }
            case "l" -> {
                List<Pick> picks = state.picks();
                views.pickLog(picks.subList(Math.max(0, picks.size() - 20), picks.size()));
            }
            case "q" -> {
                t.info("Draft saved. Resume it from the main menu.");
                return null;
            }
            case "h", "?" -> help();
            default -> t.error("Unknown command. Type h for help.");
        }
        return state;
    }

    private void resetBrowse() {
        page = 0;
        search = null;
    }

    private void help() {
        t.println("""
                  <number>   draft that player from the list
                  a          auto-pick (best fit for your roster)
                  f <POS>    filter by position (QB RB WR TE K DST); f alone clears filters
                  s <text>   search by name; s alone clears search
                  m / p      next / previous page
                  r [slot]   show a roster (yours by default)
                  t          list teams and their slots
                  b          draft board
                  l          last 20 picks
                  q          save and leave the draft
                """);
    }

    private void finish(DraftState state) {
        t.heading("Draft complete!");
        views.roster(drafts.roster(username, draftId, state.draft().userSlot()), state.draft().numTeams());
        t.println();
        if (t.confirm("Show the full draft board?")) {
            views.board(state);
        }
    }
}
