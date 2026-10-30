package com.mockdraft.web.api;

import java.time.Instant;
import java.util.List;

import com.mockdraft.core.draft.DraftState;
import com.mockdraft.core.model.Draft;
import com.mockdraft.core.model.DraftStatus;
import com.mockdraft.core.model.DraftTeam;
import com.mockdraft.core.model.Pick;
import com.mockdraft.core.model.Player;
import com.mockdraft.core.model.Roster;
import com.mockdraft.core.model.RosterSlot;
import com.mockdraft.core.model.User;

/** JSON shapes of the public API (see docs/API.md). Kept separate from core types so the wire format is explicit. */
public final class Dtos {

    private Dtos() {}

    // ---- requests ----
    public record RegisterRequest(String username, String password, String email) {}
    public record LoginRequest(String username, String password) {}
    public record EmailRequest(String email) {}
    public record TokenRequest(String token) {}
    public record ResetConfirmRequest(String token, String password) {}
    public record ChangeEmailRequest(String email, String currentPassword) {}
    public record ChangePasswordRequest(String currentPassword, String newPassword) {}
    public record PasswordRequest(String password) {}
    public record CreateDraftRequest(String teamName, Integer numTeams, Integer draftPosition) {}
    public record PickRequest(Integer playerRank) {}

    // ---- responses ----
    public record UserDto(String username, String email, Instant createdAt) {
        public static UserDto of(User u) {
            return new UserDto(u.username(), u.email(), u.createdAt());
        }
    }

    public record ValidDto(boolean valid) {}

    public record PlayerDto(int rank, String name, String position, double projectedPoints, String adp) {
        public static PlayerDto of(Player p, int numTeams) {
            return new PlayerDto(p.rank(), p.name(), p.position().name(), p.projectedPoints(), p.adp(numTeams));
        }
    }

    public record TeamDto(int slot, String name, boolean isUser) {
        public static TeamDto of(DraftTeam t) {
            return new TeamDto(t.slot(), t.name(), t.isUser());
        }
    }

    public record PickDto(int overall, int round, int pickInRound, int slot, String teamName, PlayerDto player) {
        public static PickDto of(Pick p, int numTeams) {
            return new PickDto(p.overall(), p.round(), p.pickInRound(), p.slot(), p.teamName(), PlayerDto.of(p.player(), numTeams));
        }
    }

    public record CurrentPickDto(int overall, int round, int pickInRound, int slot, String teamName, boolean isUser) {}

    public record NextUserPickDto(int overall, int round, int pickInRound, int picksUntil) {}

    public record DraftSummaryDto(long id, String teamName, int numTeams, int rounds, int userSlot, DraftStatus status,
            Instant createdAt, Instant completedAt, int picksMade, int totalPicks) {
        public static DraftSummaryDto of(Draft d) {
            return new DraftSummaryDto(d.id(), d.teamName(), d.numTeams(), d.rounds(), d.userSlot(), d.status(),
                    d.createdAt(), d.completedAt(), d.picksMade(), d.totalPicks());
        }
    }

    public record DraftStateDto(long id, String teamName, int numTeams, int rounds, int userSlot, DraftStatus status,
            Instant createdAt, Instant completedAt, int picksMade, int totalPicks,
            List<TeamDto> teams, List<PickDto> picks, CurrentPickDto currentPick, NextUserPickDto nextUserPick) {

        public static DraftStateDto of(DraftState s) {
            Draft d = s.draft();
            int n = d.numTeams();
            DraftState.Turn turn = s.currentTurn();
            DraftState.UserTurn next = s.nextUserTurn();
            DraftStatus status = s.isComplete() ? DraftStatus.COMPLETE : d.status();
            return new DraftStateDto(d.id(), d.teamName(), n, d.rounds(), d.userSlot(), status, d.createdAt(),
                    d.completedAt(), s.picks().size(), d.totalPicks(),
                    s.teams().stream().map(TeamDto::of).toList(),
                    s.picks().stream().map(p -> PickDto.of(p, n)).toList(),
                    turn == null ? null : new CurrentPickDto(turn.overall(), turn.round(), turn.pickInRound(), turn.slot(),
                            turn.teamName(), turn.isUser()),
                    next == null ? null : new NextUserPickDto(next.overall(), next.round(), next.pickInRound(), next.picksUntil()));
        }
    }

    public record RosterSlotDto(String label, PickDto pick) {
        public static RosterSlotDto of(RosterSlot s, int numTeams) {
            return new RosterSlotDto(s.label(), s.pick() == null ? null : PickDto.of(s.pick(), numTeams));
        }
    }

    public record RosterDto(int slot, String teamName, boolean isUser, List<RosterSlotDto> starters,
            List<RosterSlotDto> bench, double projectedStarterPoints) {
        public static RosterDto of(Roster r, int numTeams) {
            return new RosterDto(r.slot(), r.teamName(), r.isUser(),
                    r.starters().stream().map(s -> RosterSlotDto.of(s, numTeams)).toList(),
                    r.bench().stream().map(s -> RosterSlotDto.of(s, numTeams)).toList(),
                    r.projectedStarterPoints());
        }
    }
}
