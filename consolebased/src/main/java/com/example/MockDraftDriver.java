package com.example;

import com.example.Mock.DAO.*;
import com.example.Mock.Service.*;
import com.example.common.Logger;
import com.example.common.PlayerDataObject;
import com.example.console.PlayerModel;
import java.util.Scanner;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class MockDraftDriver {
    private static DraftServices draftServices = new DraftServices();
    private static DraftManager draftManager;
    private static int currentDraftSize = 14; // Default to 14, will be updated dynamically
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        draftManager = new DraftManager(scanner);
        
        Logger.logInfo("Fantasy Football Mock Draft Simulator (Console Version) started");
        System.out.println("Welcome to Fantasy Football Mock Draft Simulator (Console Version)");

        // User login
        String username = null;
        boolean authenticated = false;
        while (!authenticated) {
            System.out.print("Enter username: ");
            username = scanner.nextLine();
            Logger.logUserInteraction(username, "USERNAME_ENTERED", "User entered username");
            
            // Use LoginServices logic (assume static methods for now)
            if (!LoginServices.checkUser(username)) {
                System.out.println("User does not exist. Please try again.");
                Logger.logUserInteraction(username, "LOGIN_FAILED", "User does not exist");
                continue;
            }
            System.out.print("Enter password: ");
            String password = scanner.nextLine();
            Logger.logUserInteraction(username, "PASSWORD_ENTERED", "User entered password");
            
            if (LoginServices.authenticateUserPassword(username, password)) {
                authenticated = true;
                System.out.println("Login successful!");
                Logger.logAuth(username, "CONSOLE_LOGIN", "SUCCESS");
            } else {
                System.out.println("Incorrect password. Please try again.");
                Logger.logAuth(username, "CONSOLE_LOGIN", "FAILED_WRONG_PASSWORD");
            }
        }

        // Check for user's draft status
        boolean hasCurrentDraft = draftServices.checkForCurrentDraft(username);
        boolean hasPastDrafts = draftServices.checkForPastDrafts(username);
        
        System.out.println("\n=== 2026 Fantasy Football Mock Draft ===");
        System.out.println("Welcome " + username + "!");
        System.out.println();
        
        // Show draft management options
        showDraftMenu(username, hasCurrentDraft, hasPastDrafts, scanner);
        
        scanner.close();
        Logger.logInfo("Fantasy Football Mock Draft Simulator (Console Version) ended");
    }
    
    private static void showDraftMenu(String username, boolean hasCurrentDraft, boolean hasPastDrafts, Scanner scanner) {
        while (true) {
            // Refresh draft status each time to show current state
            hasCurrentDraft = draftServices.checkForCurrentDraft(username);
            hasPastDrafts = draftServices.checkForPastDrafts(username);
            
            System.out.println("\n=== Draft Management ===");
            System.out.println("1. Start New Draft");
            
            if (hasCurrentDraft) {
                System.out.println("2. Resume Last Saved Draft");
                System.out.println("3. Delete Current Draft");
            }
            
            if (hasPastDrafts) {
                System.out.println("4. View Draft History");
            }
            
            System.out.println("0. Logout");
            System.out.print("\nSelect an option: ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    startNewDraft(username, scanner);
                    break;
                case "2":
                    if (hasCurrentDraft) {
                        draftManager.resumeDraft(username);
                    } else {
                        System.out.println("Invalid option. You don't have a current draft to resume.");
                    }
                    break;
                case "3":
                    if (hasCurrentDraft) {
                        deleteCurrentDraft(username, scanner);
                    } else {
                        System.out.println("Invalid option. You don't have a current draft to delete.");
                    }
                    break;
                case "4":
                    if (hasPastDrafts) {
                        viewDraftHistory(username, scanner);
                    } else {
                        System.out.println("Invalid option. You don't have any past drafts.");
                    }
                    break;
                case "0":
                    System.out.println("Logging out. Goodbye!");
                    Logger.logAuth(username, "CONSOLE_LOGOUT", "SUCCESS");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
    
    private static void startNewDraft(String username, Scanner scanner) {
        System.out.println("\n=== Start New Draft ===");
        
        // Check if user already has a current draft
        if (draftServices.checkForCurrentDraft(username)) {
            System.out.println("You already have a current draft in progress.");
            System.out.println("Please either resume your current draft or delete it before starting a new one.");
            System.out.print("Would you like to delete your current draft and start fresh? (y/N): ");
            String confirmation = scanner.nextLine().trim().toLowerCase();
            
            if (!confirmation.equals("y") && !confirmation.equals("yes")) {
                System.out.println("New draft creation cancelled.");
                return;
            }
            
            // Delete current draft before proceeding
            boolean deleted = draftServices.deleteCurrentDraft(username);
            if (!deleted) {
                System.out.println("Failed to delete current draft. Cannot create new draft.");
                return;
            }
            System.out.println("Current draft deleted. Proceeding with new draft creation...");
        }
        
        // Get draft size
        int draftSize = 0;
        while (draftSize < 4 || draftSize > 20) {
            System.out.print("Enter number of teams (4-20): ");
            try {
                draftSize = Integer.parseInt(scanner.nextLine().trim());
                if (draftSize < 4 || draftSize > 20) {
                    System.out.println("Draft size must be between 4 and 20 teams.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
        
        // Get team name
        System.out.print("Enter your team name: ");
        String teamName = scanner.nextLine().trim();
        while (teamName.isEmpty()) {
            System.out.print("Team name cannot be empty. Enter your team name: ");
            teamName = scanner.nextLine().trim();
        }
        
        // Get draft position
        String draftPosition = "";
        while (true) {
            System.out.print("Enter desired draft position (1-" + draftSize + ") or 'R' for random: ");
            draftPosition = scanner.nextLine().trim();
            if (draftPosition.equalsIgnoreCase("R")) {
                break;
            }
            try {
                int pos = Integer.parseInt(draftPosition);
                if (pos >= 1 && pos <= draftSize) {
                    break;
                }
                System.out.println("Position must be between 1 and " + draftSize + ".");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number or 'R' for random.");
            }
        }
        
        // Start the draft with interactive drafting
        draftManager.startNewDraftWithDrafting(username, teamName, draftSize, draftPosition);
        
        Logger.logDraft(-1, username, "NEW_DRAFT_STARTED", "Successfully started interactive draft");
    }
    
    private static void deleteCurrentDraft(String username, Scanner scanner) {
        System.out.println("\n=== Delete Current Draft ===");
        System.out.print("Are you sure you want to delete your current draft? This cannot be undone. (y/N): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();
        
        if (confirmation.equals("y") || confirmation.equals("yes")) {
            boolean deleted = draftServices.deleteCurrentDraft(username);
            if (deleted) {
                System.out.println("Draft deleted successfully.");
                Logger.logDraft(-1, username, "DELETE_DRAFT", "SUCCESS");
            } else {
                System.out.println("Failed to delete draft. Please try again.");
                Logger.logDraft(-1, username, "DELETE_DRAFT", "FAILED");
            }
        } else {
            System.out.println("Draft deletion cancelled.");
            Logger.logDraft(-1, username, "DELETE_DRAFT", "CANCELLED");
        }
    }
    
    private static void viewDraftHistory(String username, Scanner scanner) {
        System.out.println("\n=== Draft History ===");
        
        List<Map<String, String>> draftHistory = draftServices.getDraftHistoryMetaData(username);
        
        if (draftHistory.isEmpty()) {
            System.out.println("No completed drafts found.");
            return;
        }
        
        System.out.println("Your completed drafts:");
        System.out.println("ID\tTeams\tDate\t\t\t\tCompleted");
        System.out.println("─".repeat(60));
        
        for (Map<String, String> draft : draftHistory) {
            System.out.printf("%s\t%s\t%s\t%s%n",
                draft.get("draft_id"),
                draft.get("num_teams"),
                draft.get("created_at"),
                draft.get("completed"));
        }
        
        System.out.println("\nOptions:");
        System.out.println("1. View detailed draft log");
        System.out.println("2. View team rosters");
        System.out.println("0. Back to main menu");
        System.out.print("Select an option: ");
        
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                viewDetailedDraftLog(username, scanner);
                break;
            case "2":
                viewTeamRosters(username, scanner);
                break;
            case "0":
                return;
            default:
                System.out.println("Invalid option. Returning to main menu.");
        }
        
        Logger.logAuth(username, "VIEW_DRAFT_HISTORY", "Viewed " + draftHistory.size() + " completed drafts");
    }
    
    private static void viewDetailedDraftLog(String username, Scanner scanner) {
        System.out.println("\n=== Detailed Draft Log ===");
        
        // Get the most recent completed draft
        int draftId = draftServices.getMostRecentCompletedDraftId(username);
        if (draftId == -1) {
            System.out.println("No completed drafts found.");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        // Set the current draft size for ADP calculations
        currentDraftSize = draftServices.getDraftSize(draftId);
        
        // Get draft picks using WebApp method
        List<PlayerDataObject> draftPicks = draftServices.getDraftHistoryDraftedPlayerLog(username, draftId);
        if (draftPicks.isEmpty()) {
            System.out.println("No draft picks found for this draft.");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        // Display draft log
        System.out.println("\n" + "=".repeat(100));
        System.out.println("                              DRAFT LOG - Draft #" + draftId);
        System.out.println("=".repeat(100));
        System.out.printf("%-8s %-8s %-20s %-25s %-8s %-8s %-12s%n", 
            "ROUND", "PICK", "TEAM", "PLAYER", "POS", "ADP", "PRED SCORE");
        System.out.println("-".repeat(100));
        
        for (PlayerDataObject player : draftPicks) {
            String[] spotParts = player.getSpotDrafted().split("\\.");
            int round = Integer.parseInt(spotParts[0]);
            int pick = Integer.parseInt(spotParts[1]);
            
            // Convert player rank to ADP format (round.pick) using correct calculation
            int playerRank = (int) player.getRank(); // Use getRank() not getAvgADP()
            int adpRound, adpPick;
            
            if (playerRank <= currentDraftSize) {
                adpRound = 1;
                adpPick = playerRank;
            } else {
                adpRound = ((playerRank - 1) / currentDraftSize) + 1;
                adpPick = ((playerRank - 1) % currentDraftSize) + 1;
            }
            String adpFormatted = String.format("%d.%02d", adpRound, adpPick);
            
            System.out.printf("%-8d %-8d %-20s %-25s %-8s %-8s %-12.1f%n",
                round,
                pick,
                player.getTeamDraftedBy(),
                player.getFullName(),
                player.getPosition(),
                adpFormatted,
                player.getPredictedScore());
        }
        System.out.println("=".repeat(100));
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
    
    private static void viewTeamRosters(String username, Scanner scanner) {
        System.out.println("\n=== Team Rosters ===");
        
        // Get the most recent completed draft
        int draftId = draftServices.getMostRecentCompletedDraftId(username);
        if (draftId == -1) {
            System.out.println("No completed drafts found.");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        // Get draft size
        int draftSize = draftServices.getDraftSize(draftId);
        if (draftSize == -1) {
            System.out.println("Failed to get draft information.");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        // Set the current draft size for ADP calculations
        currentDraftSize = draftSize;
        
        // Show team selection menu
        System.out.println("Select a team to view:");
        for (int i = 1; i <= draftSize; i++) {
            String teamName = draftServices.getUserTeamNameFromDraftSpot(draftId, i);
            System.out.println(i + ". " + teamName);
        }
        System.out.println("0. Back to main menu");
        System.out.print("Enter team number: ");
        
        String choice = scanner.nextLine().trim();
        if (choice.equals("0")) {
            return;
        }
        
        try {
            int teamIndex = Integer.parseInt(choice);
            if (teamIndex < 1 || teamIndex > draftSize) {
                System.out.println("Invalid team number.");
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
                return;
            }
            
            // Get team players using WebApp method
            List<Integer> teamArrayInt = draftServices.createTeamIntArrayFromDB(draftId, teamIndex);
            List<PlayerDataObject> teamPlayerList = draftServices.createPlayerModelListFromIntList(teamArrayInt, draftSize);
            String teamName = draftServices.getUserTeamNameFromDraftSpot(draftId, teamIndex);
            
            // Calculate draft positions for each player
            int round = 1;
            int pickOdd = teamIndex;
            int pickEven = Math.abs(teamIndex - draftSize) + 1;
            int pick = pickOdd;
            for (PlayerDataObject player : teamPlayerList) {
                player.setSpotDrafted(round + "." + pick);
                round++;
                pick = round % 2 == 0 ? pickEven : pickOdd;
            }
            
            // Display team roster
            System.out.println("\n" + "=".repeat(80));
            System.out.println("TEAM: " + teamName + " - Draft #" + draftId);
            System.out.println("=".repeat(80));
            
            // Group players by position and sort by predicted score
            Map<String, List<PlayerDataObject>> playersByPosition = new HashMap<>();
            for (PlayerDataObject player : teamPlayerList) {
                String position = player.getPosition();
                playersByPosition.computeIfAbsent(position, k -> new ArrayList<>()).add(player);
            }
            
            // Sort each position by predicted score (best first)
            for (List<PlayerDataObject> positionPlayers : playersByPosition.values()) {
                positionPlayers.sort((p1, p2) -> Double.compare(p2.getPredictedScore(), p1.getPredictedScore()));
            }
            
            System.out.println("\n🏈 STARTERS:");
            System.out.println("-".repeat(60));
            
            List<PlayerDataObject> usedStarters = new ArrayList<>();
            
            // QB (1 starter)
            if (playersByPosition.containsKey("QB") && !playersByPosition.get("QB").isEmpty()) {
                PlayerDataObject qb = playersByPosition.get("QB").get(0);
                displayPlayerRow("QB", qb);
                usedStarters.add(qb);
            }
            
            // RB (2 starters)
            if (playersByPosition.containsKey("RB")) {
                List<PlayerDataObject> rbs = playersByPosition.get("RB");
                for (int i = 0; i < Math.min(rbs.size(), 2); i++) {
                    PlayerDataObject rb = rbs.get(i);
                    displayPlayerRow("RB" + (i + 1), rb);
                    usedStarters.add(rb);
                }
            }
            
            // WR (2 starters)
            if (playersByPosition.containsKey("WR")) {
                List<PlayerDataObject> wrs = playersByPosition.get("WR");
                for (int i = 0; i < Math.min(wrs.size(), 2); i++) {
                    PlayerDataObject wr = wrs.get(i);
                    displayPlayerRow("WR" + (i + 1), wr);
                    usedStarters.add(wr);
                }
            }
            
            // TE (1 starter)
            if (playersByPosition.containsKey("TE") && !playersByPosition.get("TE").isEmpty()) {
                PlayerDataObject te = playersByPosition.get("TE").get(0);
                displayPlayerRow("TE", te);
                usedStarters.add(te);
            }
            
            // FLEX (best remaining RB/WR/TE)
            PlayerDataObject flexPlayer = null;
            double bestFlexScore = 0;
            String flexPosition = "";
            
            // Check remaining RBs
            if (playersByPosition.containsKey("RB")) {
                List<PlayerDataObject> rbs = playersByPosition.get("RB");
                for (PlayerDataObject rb : rbs) {
                    if (!usedStarters.contains(rb) && rb.getPredictedScore() > bestFlexScore) {
                        flexPlayer = rb;
                        bestFlexScore = rb.getPredictedScore();
                        flexPosition = "RB";
                    }
                }
            }
            
            // Check remaining WRs
            if (playersByPosition.containsKey("WR")) {
                List<PlayerDataObject> wrs = playersByPosition.get("WR");
                for (PlayerDataObject wr : wrs) {
                    if (!usedStarters.contains(wr) && wr.getPredictedScore() > bestFlexScore) {
                        flexPlayer = wr;
                        bestFlexScore = wr.getPredictedScore();
                        flexPosition = "WR";
                    }
                }
            }
            
            // Check remaining TEs
            if (playersByPosition.containsKey("TE")) {
                List<PlayerDataObject> tes = playersByPosition.get("TE");
                for (PlayerDataObject te : tes) {
                    if (!usedStarters.contains(te) && te.getPredictedScore() > bestFlexScore) {
                        flexPlayer = te;
                        bestFlexScore = te.getPredictedScore();
                        flexPosition = "TE";
                    }
                }
            }
            
            if (flexPlayer != null) {
                displayPlayerRow("FLEX(" + flexPosition + ")", flexPlayer);
                usedStarters.add(flexPlayer);
            }
            
            // K (1 starter)
            if (playersByPosition.containsKey("K") && !playersByPosition.get("K").isEmpty()) {
                PlayerDataObject k = playersByPosition.get("K").get(0);
                displayPlayerRow("K", k);
                usedStarters.add(k);
            }
            
            // DST (1 starter)
            if (playersByPosition.containsKey("DST") && !playersByPosition.get("DST").isEmpty()) {
                PlayerDataObject dst = playersByPosition.get("DST").get(0);
                displayPlayerRow("DST", dst);
                usedStarters.add(dst);
            }
            
            // Display bench players
            System.out.println("\n🪑 BENCH:");
            System.out.println("-".repeat(60));
            
            List<PlayerDataObject> benchPlayers = new ArrayList<>();
            for (PlayerDataObject player : teamPlayerList) {
                if (!usedStarters.contains(player)) {
                    benchPlayers.add(player);
                }
            }
            
            // Sort bench by predicted score (best first)
            benchPlayers.sort((p1, p2) -> Double.compare(p2.getPredictedScore(), p1.getPredictedScore()));
            
            for (PlayerDataObject player : benchPlayers) {
                displayPlayerRow("BENCH", player);
            }
            
            System.out.println("=".repeat(80));
            
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
    
    private static void displayPlayerRow(String rosterSpot, PlayerDataObject player) {
        // Convert player rank to ADP format (round.pick) using correct calculation
        int playerRank = (int) player.getRank(); // Use getRank() not getAvgADP()
        int adpRound, adpPick;
        
        if (playerRank <= currentDraftSize) {
            adpRound = 1;
            adpPick = playerRank;
        } else {
            adpRound = ((playerRank - 1) / currentDraftSize) + 1;
            adpPick = ((playerRank - 1) % currentDraftSize) + 1;
        }
        String adpFormatted = String.format("%d.%02d", adpRound, adpPick);
        
        System.out.printf("%-12s %-25s (ADP: %s, Picked: %s, Score: %.1f)%n",
            rosterSpot,
            player.getFullName(),
            adpFormatted,
            player.getSpotDrafted(),
            player.getPredictedScore());
    }
}
