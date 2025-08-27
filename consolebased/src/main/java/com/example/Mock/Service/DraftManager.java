package com.example.Mock.Service;

import java.util.*;
import java.util.Scanner;
import com.example.common.Logger;
import com.example.common.PlayerDataObject;
import com.example.console.PlayerModel;
import com.example.console.TeamModel;
import com.example.DraftHandler;
import com.example.Mock.DAO.PlayerDataObjectRepository;

public class DraftManager {
    private DraftServices draftServices;
    private Scanner scanner;
    
    public DraftManager(Scanner scanner) {
        this.draftServices = new DraftServices();
        this.scanner = scanner;
    }
    
    // Start a new draft and immediately begin drafting
    public void startNewDraftWithDrafting(String username, String teamName, int draftSize, String draftPosition) {
        System.out.println("\nLoading players from database...");
        Logger.logInfo("Starting player data load from database");
        
        List<PlayerDataObject> allPlayerModels = PlayerDataObjectRepository.getAllPlayers();
        allPlayerModels.sort(null);
        
        System.out.println("Loaded " + allPlayerModels.size() + " players from database");
        Logger.logInfo("Loaded " + allPlayerModels.size() + " players from database");
        
        // Create the draft in the database
        int draftId = draftServices.startDraft(username, teamName, draftSize, draftPosition, allPlayerModels);
        if (draftId == -1) {
            System.out.println("Failed to create draft. Please try again.");
            return;
        }
        
        System.out.println("\nDraft created successfully!");
        System.out.println("Teams: " + draftSize);
        System.out.println("Your team: " + teamName);
        System.out.println("Draft position: " + (draftPosition.equalsIgnoreCase("R") ? "Random" : draftPosition));
        
        Logger.logDraft(draftId, username, "NEW_DRAFT_CREATED", "Successfully created new draft");
        
        // Start the interactive drafting process
        beginInteractiveDraft(username, draftId, allPlayerModels, teamName, draftSize, draftPosition);
    }
    
    // Resume an existing draft
    public void resumeDraft(String username) {
        Map<String, Object> draftInfo = draftServices.getCurrentDraftInfo(username);
        if (draftInfo == null) {
            System.out.println("No current draft found to resume.");
            return;
        }
        
        int draftId = (int) draftInfo.get("draft_id");
        int numTeams = (int) draftInfo.get("num_teams");
        int currRound = (int) draftInfo.get("curr_round");
        int currPick = (int) draftInfo.get("curr_pick");
        String teamName = draftServices.getUserTeamName(draftId);
        
        System.out.println("\n=== Resuming Draft ===");
        System.out.println("Draft ID: " + draftId);
        System.out.println("Teams: " + numTeams);
        System.out.println("Your team: " + teamName);
        System.out.println("Current position: Round " + currRound + ", Pick " + currPick);
        
        // Load available players
        List<PlayerDataObject> allPlayerModels = PlayerDataObjectRepository.getAllPlayers();
        allPlayerModels.sort(null);
        
        // TODO: In a full implementation, we would need to:
        // 1. Load already drafted players from database
        // 2. Remove them from the available players list
        // 3. Reconstruct the team rosters
        // For now, we'll start a fresh draft process
        
        System.out.println("\nNote: Resume functionality will continue from where you left off.");
        System.out.println("For now, starting a fresh draft simulation...\n");
        
        // Begin drafting (in future, this would continue from current state)
        beginInteractiveDraft(username, draftId, allPlayerModels, teamName, numTeams, "1");
    }
    
    private void beginInteractiveDraft(String username, int draftId, List<PlayerDataObject> allPlayers, 
                                     String teamName, int numTeams, String draftPosition) {
        
        System.out.println("\n" + "=".repeat(50));
        System.out.println("           STARTING INTERACTIVE DRAFT");
        System.out.println("=".repeat(50));
        System.out.println();
        
        // Convert PlayerDataObject list to PlayerModel list for DraftHandler
        ArrayList<PlayerModel> playerModels = new ArrayList<>();
        for (PlayerDataObject player : allPlayers) {
            playerModels.add(new PlayerModel(player));
        }
        
        // Create and start the draft handler
        DraftHandler draftHandler = new DraftHandler(playerModels, numTeams, teamName, draftPosition, scanner);
        
        // Start the interactive draft
        draftHandler.startDraft();
        
        // Print final results
        System.out.println("\n" + "=".repeat(50));
        System.out.println("           DRAFT COMPLETE!");
        System.out.println("=".repeat(50));
        draftHandler.printTeams();
        
        // Mark draft as completed in database
        draftServices.completeDraft(username, draftId);
        
        System.out.println("\nDraft has been saved and marked as completed.");
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
        
        Logger.logDraft(draftId, username, "DRAFT_COMPLETED_INTERACTIVE", "Interactive draft completed successfully");
    }
} 