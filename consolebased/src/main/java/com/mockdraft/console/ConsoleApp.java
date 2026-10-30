package com.mockdraft.console;

import java.util.List;
import java.util.Optional;

import com.mockdraft.core.MockDraftCore;
import com.mockdraft.core.draft.DraftState;
import com.mockdraft.core.model.Draft;
import com.mockdraft.core.model.User;
import com.mockdraft.core.service.AuthService;
import com.mockdraft.core.service.AuthenticationException;
import com.mockdraft.core.service.ConflictException;
import com.mockdraft.core.service.DraftService;
import com.mockdraft.core.service.NotFoundException;
import com.mockdraft.core.service.ValidationException;
import com.mockdraft.core.service.Validators;

/** Menus for the console client. All behaviour lives in the shared core services. */
public final class ConsoleApp {

    private final Terminal t;
    private final Views views;
    private final AuthService auth;
    private final DraftService drafts;

    public ConsoleApp(Terminal t, MockDraftCore core) {
        this.t = t;
        this.views = new Views(t);
        this.auth = core.auth();
        this.drafts = core.drafts();
    }

    public void run() {
        banner();
        while (true) {
            t.heading("Welcome");
            t.println("  1  Sign in");
            t.println("  2  Create account");
            t.println("  0  Quit");
            switch (t.prompt("Choose: ")) {
                case "1" -> signIn().ifPresent(this::mainMenu);
                case "2" -> createAccount().ifPresent(this::mainMenu);
                case "0", "q" -> {
                    t.info("Good luck this season!");
                    return;
                }
                default -> t.error("Choose 1, 2 or 0.");
            }
        }
    }

    private void banner() {
        t.println(t.bold(t.accent("""

                  __  __            _    ____            __ _     ____
                 |  \\/  | ___   ___| | _|  _ \\ _ __ __ _/ _| |_  |  _ \\ _ __ ___
                 | |\\/| |/ _ \\ / __| |/ / | | | '__/ _` | |_| __| | |_) | '__/ _ \\
                 | |  | | (_) | (__|   <| |_| | | | (_| |  _| |_  |  __/| | | (_) |
                 |_|  |_|\\___/ \\___|_|\\_\\____/|_|  \\__,_|_|  \\__| |_|   |_|  \\___/
                """)));
        t.info("  2026 fantasy football mock draft simulator");
    }

    private Optional<String> signIn() {
        String username = t.prompt("Username: ");
        String password = t.password("Password: ");
        try {
            User user = auth.authenticate(username, password);
            t.success("Signed in as " + user.username());
            return Optional.of(user.username());
        } catch (AuthenticationException e) {
            t.error(e.getMessage());
            return Optional.empty();
        }
    }

    private Optional<String> createAccount() {
        t.info("Username: 3-30 letters, numbers or underscores. Password: at least " + Validators.PASSWORD_MIN + " characters.");
        String username = t.prompt("Username: ");
        String password = t.password("Password: ");
        String again = t.password("Confirm password: ");
        if (!password.equals(again)) {
            t.error("Passwords do not match.");
            return Optional.empty();
        }
        String email = t.prompt("Email for password recovery (optional): ");
        try {
            User user = auth.register(username, password, email);
            t.success("Account created. Welcome, " + user.username() + "!");
            return Optional.of(user.username());
        } catch (ValidationException | ConflictException e) {
            t.error(e.getMessage());
            return Optional.empty();
        }
    }

    private void mainMenu(String username) {
        while (true) {
            Optional<DraftState> current = drafts.current(username);
            t.heading("Main menu · " + username);
            current.ifPresent(s -> t.println(t.yellow(String.format("  In progress: %s — pick %d of %d",
                    s.draft().teamName(), s.picks().size() + 1, s.draft().totalPicks()))));
            t.println(current.isPresent() ? "  1  Resume draft" : "  1  Start a new draft");
            t.println("  2  Draft history");
            if (current.isPresent()) {
                t.println("  3  Delete in-progress draft");
            }
            t.println("  4  Account settings");
            t.println("  0  Sign out");
            String choice = t.prompt("Choose: ");
            try {
                switch (choice) {
                    case "1" -> {
                        long id = current.isPresent() ? current.get().draft().id() : newDraft(username);
                        if (id > 0) {
                            new DraftRoom(t, drafts, username, id).run();
                        }
                    }
                    case "2" -> history(username);
                    case "3" -> {
                        if (current.isPresent() && t.confirm("Delete your in-progress draft? This cannot be undone.")) {
                            drafts.delete(username, current.get().draft().id());
                            t.success("Draft deleted.");
                        }
                    }
                    case "4" -> {
                        if (account(username)) {
                            return;
                        }
                    }
                    case "0" -> {
                        t.info("Signed out.");
                        return;
                    }
                    default -> t.error("Choose one of the listed options.");
                }
            } catch (ValidationException | ConflictException | NotFoundException e) {
                t.error(e.getMessage());
            }
        }
    }

    /** Returns the new draft id, or -1 if the user backed out. */
    private long newDraft(String username) {
        t.heading("New draft");
        String teamName = t.prompt("Team name: ");
        try {
            Validators.teamName(teamName);
        } catch (ValidationException e) {
            t.error(e.getMessage());
            return -1;
        }
        Integer size = t.promptInt("League size (" + Validators.MIN_TEAMS + "-" + Validators.MAX_TEAMS + ") [12]: ",
                Validators.MIN_TEAMS, Validators.MAX_TEAMS, true);
        int numTeams = size == null ? 12 : size;
        Integer position = t.promptInt("Draft position (1-" + numTeams + ", blank = random): ", 1, numTeams, true);
        DraftState state = drafts.create(username, teamName, numTeams, position);
        t.success("Draft created — you pick from slot " + state.draft().userSlot() + " of " + numTeams + ".");
        return state.draft().id();
    }

    private void history(String username) {
        while (true) {
            List<Draft> list = drafts.list(username);
            t.heading("Draft history");
            if (list.isEmpty()) {
                t.info("  No drafts yet.");
                return;
            }
            t.println(t.dim(String.format("  %-6s %-16s %-24s %9s  %-7s  %-11s  %s", "ID", "DATE", "TEAM", "SIZE", "SLOT", "STATUS", "PICKS")));
            list.forEach(views::draftRow);
            String raw = t.prompt("Draft ID to open (blank = back): ");
            if (raw.isEmpty()) {
                return;
            }
            try {
                reviewDraft(username, Long.parseLong(raw));
            } catch (NumberFormatException e) {
                t.error("Enter a draft ID from the list.");
            } catch (NotFoundException e) {
                t.error(e.getMessage());
            }
        }
    }

    private void reviewDraft(String username, long id) {
        while (true) {
            DraftState state = drafts.get(username, id);
            t.heading("Draft #" + id + " · " + state.draft().teamName() + (state.isComplete() ? "" : " (in progress)"));
            t.println("  1  Pick log");
            t.println("  2  Team rosters");
            t.println("  3  Draft board");
            if (!state.isComplete()) {
                t.println("  4  Resume this draft");
            }
            t.println("  9  Delete this draft");
            t.println("  0  Back");
            switch (t.prompt("Choose: ")) {
                case "1" -> views.pickLog(state.picks());
                case "2" -> {
                    state.teams().forEach(team -> t.println(String.format("  %2d  %s%s", team.slot(), team.name(), team.isUser() ? "  (you)" : "")));
                    Integer slot = t.promptInt("Team slot (blank = yours): ", 1, state.draft().numTeams(), true);
                    views.roster(drafts.roster(username, id, slot == null ? state.draft().userSlot() : slot), state.draft().numTeams());
                }
                case "3" -> views.board(state);
                case "4" -> {
                    if (!state.isComplete()) {
                        new DraftRoom(t, drafts, username, id).run();
                    }
                }
                case "9" -> {
                    if (t.confirm("Delete draft #" + id + "? This cannot be undone.")) {
                        drafts.delete(username, id);
                        t.success("Draft deleted.");
                        return;
                    }
                }
                case "0", "" -> {
                    return;
                }
                default -> t.error("Choose one of the listed options.");
            }
        }
    }

    /** Returns true if the account was deleted (caller should sign out). */
    private boolean account(String username) {
        User user = auth.currentUser(username).orElseThrow();
        t.heading("Account · " + user.username());
        t.println("  Email: " + (user.email() == null ? t.dim("not set") : user.email()));
        t.println("  1  Change password");
        t.println("  2  Change email");
        t.println("  9  Delete account");
        t.println("  0  Back");
        try {
            switch (t.prompt("Choose: ")) {
                case "1" -> {
                    String current = t.password("Current password: ");
                    String next = t.password("New password: ");
                    if (!next.equals(t.password("Confirm new password: "))) {
                        t.error("Passwords do not match.");
                        return false;
                    }
                    auth.changePassword(username, current, next, null);
                    t.success("Password changed. Other sessions (including the web app) were signed out.");
                }
                case "2" -> {
                    String current = t.password("Current password: ");
                    String email = t.prompt("New email (blank to remove): ");
                    User updated = auth.changeEmail(username, current, email);
                    t.success("Email is now " + (updated.email() == null ? "not set" : updated.email()) + ".");
                }
                case "9" -> {
                    if (t.confirm("Permanently delete your account and all drafts?")) {
                        auth.deleteAccount(username, t.password("Password: "));
                        t.success("Account deleted.");
                        return true;
                    }
                }
                default -> { }
            }
        } catch (ValidationException | ConflictException e) {
            t.error(e.getMessage());
        }
        return false;
    }
}
