package com.mockdraft.console;

import java.util.List;

import com.mockdraft.core.draft.DraftState;
import com.mockdraft.core.model.Draft;
import com.mockdraft.core.model.DraftTeam;
import com.mockdraft.core.model.Pick;
import com.mockdraft.core.model.Player;
import com.mockdraft.core.model.Roster;
import com.mockdraft.core.model.RosterSlot;

/** Text renderings of core objects. */
final class Views {

    private static final java.time.format.DateTimeFormatter DATE =
            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(java.time.ZoneId.systemDefault());

    private final Terminal t;

    Views(Terminal t) {
        this.t = t;
    }

    void players(List<Player> players, int offset, int numTeams) {
        t.println(t.dim(String.format("  %-4s %-26s %-4s %8s %7s %6s", "#", "PLAYER", "POS", "PROJ", "ADP", "RANK")));
        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            t.println(String.format("  %-4s %-26s ", t.bold(String.valueOf(offset + i + 1)), truncate(p.name(), 26))
                    + t.position(p.position().name())
                    + String.format("  %8.1f %7s %6d", p.projectedPoints(), p.adp(numTeams), p.rank()));
        }
    }

    void pickLine(Pick p, boolean highlight) {
        String line = String.format("  %-6s %-22s %-26s ", p.label(), truncate(p.teamName(), 22), truncate(p.player().name(), 26))
                + t.position(p.player().position().name())
                + String.format(" %7.1f", p.player().projectedPoints());
        t.println(highlight ? t.green(line) : line);
    }

    void pickLog(List<Pick> picks) {
        t.println(t.dim(String.format("  %-6s %-22s %-26s %-3s %7s", "PICK", "TEAM", "PLAYER", "POS", "PROJ")));
        for (Pick p : picks) {
            pickLine(p, false);
        }
    }

    void roster(Roster r, int numTeams) {
        t.heading(r.teamName() + (r.isUser() ? "  (you)" : "") + "  ·  projected starters " + r.projectedStarterPoints());
        for (RosterSlot s : r.starters()) {
            rosterRow(s);
        }
        if (!r.bench().isEmpty()) {
            t.println(t.dim("  ── bench ──"));
            for (RosterSlot s : r.bench()) {
                rosterRow(s);
            }
        }
    }

    private void rosterRow(RosterSlot s) {
        if (s.pick() == null) {
            t.println(String.format("  %-5s %s", s.label(), t.dim("— empty —")));
            return;
        }
        Player p = s.pick().player();
        t.println(String.format("  %-5s %-26s ", s.label(), truncate(p.name(), 26)) + t.position(p.position().name())
                + String.format(" %7.1f  picked %s", p.projectedPoints(), s.pick().label()));
    }

    /** Rounds down, teams across; each cell shows a short player name and position. */
    void board(DraftState state) {
        List<DraftTeam> teams = state.teams();
        int width = teams.size() > 10 ? 9 : 13;
        StringBuilder header = new StringBuilder("  RD ");
        for (DraftTeam team : teams) {
            String label = truncate(team.isUser() ? "★" + team.name() : team.name(), width - 1);
            header.append(String.format("%-" + width + "s", label));
        }
        t.println(t.dim(header.toString()));
        Pick[][] grid = new Pick[state.draft().rounds()][teams.size()];
        for (Pick p : state.picks()) {
            grid[p.round() - 1][p.slot() - 1] = p;
        }
        int lastRound = state.picks().isEmpty() ? 1 : state.picks().get(state.picks().size() - 1).round();
        for (int r = 0; r < lastRound; r++) {
            StringBuilder row = new StringBuilder(String.format("  %-3d", r + 1));
            for (int s = 0; s < teams.size(); s++) {
                Pick p = grid[r][s];
                String cell = p == null ? "·" : shortName(p.player().name()) + " " + p.player().position().name();
                cell = String.format("%-" + width + "s", truncate(cell, width - 1));
                row.append(teams.get(s).isUser() && p != null ? t.green(cell) : cell);
            }
            t.println(row.toString());
        }
    }

    void draftRow(Draft d) {
        String status = d.isComplete() ? t.green("complete   ") : t.yellow("in progress");
        t.println(String.format("  %-6d %-16s %-24s %3d teams  slot %-2d  %s  %d/%d", d.id(), DATE.format(d.createdAt()),
                truncate(d.teamName(), 24), d.numTeams(), d.userSlot(), status, d.picksMade(), d.totalPicks()));
    }

    static String truncate(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max - 1) + "…";
    }

    private static String shortName(String name) {
        int space = name.indexOf(' ');
        return space > 0 ? name.charAt(0) + "." + name.substring(space + 1) : name;
    }
}
