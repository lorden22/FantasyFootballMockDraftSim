package com.example;

import com.example.Mock.DAO.*;
import com.example.Mock.Service.*;
import com.example.common.Logger;
import com.example.common.PlayerDataObject;
import com.example.console.PlayerModel;
import java.util.Scanner;
import java.util.List;
import java.util.Map;

public class MockDraftDriver {
    private static DraftServices draftServices = new DraftServices();
    private static DraftManager draftManager;
    
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
        
        System.out.println("\n=== 2024 Fantasy Football Mock Draft ===");
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
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
        
        Logger.logAuth(username, "VIEW_DRAFT_HISTORY", "Viewed " + draftHistory.size() + " completed drafts");
    }
}
