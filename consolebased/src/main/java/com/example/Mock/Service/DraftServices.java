package com.example.Mock.Service;

import java.sql.*;
import java.util.*;
import com.example.common.Logger;
import com.example.common.PlayerDataObject;
import com.example.Mock.DAO.DBConnection;
import com.example.Mock.DAO.DraftDataObjectDAO;
import com.example.Mock.DAO.TeamDataObject;

public class DraftServices {
    
    // Check if user has a current (incomplete) draft
    public boolean checkForCurrentDraft(String username) {
        Logger.logAuth(username, "CHECK_CURRENT_DRAFT", "ATTEMPT");
        String sql = "SELECT COUNT(*) FROM drafts WHERE username = ? AND complete_status = 0";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                boolean hasCurrentDraft = rs.getInt(1) > 0;
                Logger.logAuth(username, "CHECK_CURRENT_DRAFT", hasCurrentDraft ? "FOUND" : "NOT_FOUND");
                return hasCurrentDraft;
            }
        } catch (SQLException e) {
            Logger.logError("Failed to check for current draft for user " + username + ": " + e.getMessage());
        }
        Logger.logAuth(username, "CHECK_CURRENT_DRAFT", "ERROR");
        return false;
    }
    
    // Check if user has past (completed) drafts
    public boolean checkForPastDrafts(String username) {
        Logger.logAuth(username, "CHECK_PAST_DRAFTS", "ATTEMPT");
        String sql = "SELECT COUNT(*) FROM drafts WHERE username = ? AND complete_status = 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                boolean hasPastDrafts = rs.getInt(1) > 0;
                Logger.logAuth(username, "CHECK_PAST_DRAFTS", hasPastDrafts ? "FOUND" : "NOT_FOUND");
                return hasPastDrafts;
            }
        } catch (SQLException e) {
            Logger.logError("Failed to check for past drafts for user " + username + ": " + e.getMessage());
        }
        Logger.logAuth(username, "CHECK_PAST_DRAFTS", "ERROR");
        return false;
    }
    
    // Get the user's most recent draft ID
    public int getUserMostRecentDraftID(String username) {
        String sql = "SELECT draft_id FROM drafts WHERE username = ? ORDER BY draft_id DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("draft_id");
            }
        } catch (SQLException e) {
            Logger.logError("Failed to get most recent draft ID for user " + username + ": " + e.getMessage());
        }
        return -1;
    }
    
    // Get current draft information
    public Map<String, Object> getCurrentDraftInfo(String username) {
        Map<String, Object> draftInfo = new HashMap<>();
        String sql = "SELECT draft_id, num_teams, curr_round, curr_pick FROM drafts WHERE username = ? AND complete_status = 0 ORDER BY draft_id DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                draftInfo.put("draft_id", rs.getInt("draft_id"));
                draftInfo.put("num_teams", rs.getInt("num_teams"));
                draftInfo.put("curr_round", rs.getInt("curr_round"));
                draftInfo.put("curr_pick", rs.getInt("curr_pick"));
                Logger.logAuth(username, "GET_CURRENT_DRAFT_INFO", "Retrieved draft info");
                return draftInfo;
            }
        } catch (SQLException e) {
            Logger.logError("Failed to get current draft info for user " + username + ": " + e.getMessage());
        }
        return null;
    }
    
    // Get user's team name for a draft
    public String getUserTeamName(int draftId) {
        String sql = "SELECT team_name FROM teams WHERE draft_id = ? AND user_team = TRUE LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, draftId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("team_name");
            }
        } catch (SQLException e) {
            Logger.logError("Failed to get user team name for draft " + draftId + ": " + e.getMessage());
        }
        return "Your Team";
    }
    
    // Delete the user's current draft
    public boolean deleteCurrentDraft(String username) {
        Logger.logDraft(-1, username, "DELETE_CURRENT_DRAFT", "ATTEMPT");
        int draftId = getUserMostRecentDraftID(username);
        if (draftId == -1) {
            Logger.logDraft(-1, username, "DELETE_CURRENT_DRAFT", "NO_DRAFT_FOUND");
            return false;
        }
        
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            // Delete teams associated with this draft
            String deleteTeamsSql = "DELETE FROM teams WHERE draft_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteTeamsSql)) {
                stmt.setInt(1, draftId);
                stmt.executeUpdate();
            }
            
            // Delete the draft itself
            String deleteDraftSql = "DELETE FROM drafts WHERE draft_id = ? AND username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteDraftSql)) {
                stmt.setInt(1, draftId);
                stmt.setString(2, username);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    conn.commit();
                    Logger.logDraft(draftId, username, "DELETE_CURRENT_DRAFT", "SUCCESS");
                    return true;
                }
            }
            
            conn.rollback();
        } catch (SQLException e) {
            Logger.logError("Failed to delete current draft for user " + username + ": " + e.getMessage());
        }
        
        Logger.logDraft(draftId, username, "DELETE_CURRENT_DRAFT", "FAILED");
        return false;
    }
    
    // Get draft history metadata for display
    public List<Map<String, String>> getDraftHistoryMetaData(String username) {
        List<Map<String, String>> draftHistory = new ArrayList<>();
        String sql = "SELECT draft_id, num_teams, date, complete_status FROM drafts WHERE username = ? AND complete_status = 1 ORDER BY draft_id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, String> draftInfo = new HashMap<>();
                draftInfo.put("draft_id", String.valueOf(rs.getInt("draft_id")));
                draftInfo.put("num_teams", String.valueOf(rs.getInt("num_teams")));
                draftInfo.put("created_at", rs.getTimestamp("date").toString());
                draftInfo.put("completed", rs.getInt("complete_status") == 1 ? "true" : "false");
                draftHistory.add(draftInfo);
            }
            Logger.logAuth(username, "GET_DRAFT_HISTORY", "LOADED_" + draftHistory.size() + "_DRAFTS");
        } catch (SQLException e) {
            Logger.logError("Failed to get draft history for user " + username + ": " + e.getMessage());
        }
        return draftHistory;
    }

    public int startDraft(String username, String teamName, int draftSize, String desiredDraftPick, List<PlayerDataObject> allPlayers) {
        Logger.logDraft(-1, username, "START_DRAFT", "Initiating draft with " + draftSize + " teams");
        
        // Create a draft record in the database
        int draftId = createDraft(username, draftSize);
        if (draftId == -1) {
            Logger.logError("Failed to create draft in database for user: " + username);
            return -1;
        }
        
        Logger.logDraft(draftId, username, "DRAFT_CREATED", "Draft created successfully");
        
        // Create teams in the database
        int desiredDraftPickInt = desiredDraftPick.equalsIgnoreCase("R") ? (int)(Math.random() * draftSize) + 1 : Integer.parseInt(desiredDraftPick);
        Logger.logDraft(draftId, username, "DRAFT_POSITION", "Selected position: " + desiredDraftPickInt);
        
        for (int i = 1; i <= draftSize; i++) {
            boolean isUserTeam = (i == desiredDraftPickInt);
            createTeam(draftId, isUserTeam ? teamName : ("CPU Team " + i), isUserTeam, i);
        }
        
        Logger.logDraft(draftId, username, "TEAMS_CREATED", "Created " + draftSize + " teams");
        Logger.logInfo("Draft " + draftId + " ready with " + allPlayers.size() + " available players");
        
        Logger.logDraft(draftId, username, "DRAFT_SETUP_COMPLETE", "Draft setup completed successfully");
        return draftId;
    }
    
    // Mark draft as completed
    public boolean completeDraft(String username, int draftId) {
        String sql = "UPDATE drafts SET complete_status = 1 WHERE draft_id = ? AND username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, draftId);
            stmt.setString(2, username);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                Logger.logDraft(draftId, username, "DRAFT_COMPLETED", "Draft marked as completed");
                return true;
            }
        } catch (SQLException e) {
            Logger.logError("Failed to complete draft " + draftId + ": " + e.getMessage());
        }
        return false;
    }

    private int createDraft(String username, int numTeams) {
        String sql = "INSERT INTO drafts (username, num_teams, curr_round, curr_pick, complete_status) VALUES (?, ?, 1, 1, 0)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, username);
            stmt.setInt(2, numTeams);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int draftId = rs.getInt(1);
                Logger.logDraft(draftId, username, "DRAFT_DB_CREATED", "Database record created");
                return draftId;
            }
        } catch (SQLException e) {
            Logger.logError("Failed to create draft in database: " + e.getMessage());
        }
        return -1;
    }

    private void createTeam(int draftId, String teamName, boolean isUserTeam, int teamNumber) {
        String sql = "INSERT INTO teams (draft_id, team_name, user_team, team_number) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, draftId);
            stmt.setString(2, teamName);
            stmt.setBoolean(3, isUserTeam);
            stmt.setInt(4, teamNumber);
            stmt.executeUpdate();
            Logger.logTeamUpdate(draftId, isUserTeam ? "USER" : "SYSTEM", teamName, "TEAM_DB_CREATED");
        } catch (SQLException e) {
            Logger.logError("Failed to create team " + teamName + ": " + e.getMessage());
        }
    }

    private List<TeamDataObject> getTeamsForDraft(int draftId) {
        List<TeamDataObject> teams = new ArrayList<>();
        String sql = "SELECT team_name, user_team, team_number FROM teams WHERE draft_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, draftId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String teamName = rs.getString("team_name");
                boolean userTeam = rs.getBoolean("user_team");
                int teamNumber = rs.getInt("team_number");
                teams.add(new TeamDataObject(teamName, userTeam, teamNumber));
            }
            Logger.logDraft(draftId, "SYSTEM", "TEAMS_LOADED", "Loaded " + teams.size() + " teams from database");
        } catch (SQLException e) {
            Logger.logError("Failed to load teams for draft " + draftId + ": " + e.getMessage());
        }
        return teams;
    }
    
    // Get draft picks for a specific draft (EXACT COPY from WebApp)
    public List<PlayerDataObject> getDraftHistoryDraftedPlayerLog(String username, int draftID) {
        ArrayList<PlayerDataObject> allPlayers = new ArrayList<>();
        int draftSize = this.getDraftSize(draftID);
        for (int i = 1; i <= draftSize; i++) {
            List<Integer> teamArrayInt = this.createTeamIntArrayFromDB(draftID, i);
            List<PlayerDataObject> teamPlayerList = this.createPlayerModelListFromIntList(teamArrayInt, draftSize);
            String teamName = getUserTeamNameFromDraftSpot(draftID, i);
            String teamDraftSpot = String.valueOf(i);
            int round = 1;
            int pickOdd = Integer.parseInt(teamDraftSpot);
            int pickEven = Math.abs(Integer.parseInt(teamDraftSpot) - draftSize) + 1;
            int pick = pickOdd;
            for(PlayerDataObject player : teamPlayerList) {
                player.setSpotDrafted(round+"."+pick);
                player.setTeamDraftedBy(teamName);
                round++;
                pick = round % 2 == 0 ? pickEven : pickOdd;
            }
            allPlayers.addAll(teamPlayerList);
            Logger.logDraft(draftID, username, "GET_DRAFT_HISTORY_PLAYERS", "Team " + i + " has " + teamPlayerList.size() + " players");
        }
        // Sort by spot drafted
        allPlayers.sort((p1, p2) -> {
            String[] spot1 = p1.getSpotDrafted().split("\\.");
            String[] spot2 = p2.getSpotDrafted().split("\\.");
            int round1 = Integer.parseInt(spot1[0]);
            int round2 = Integer.parseInt(spot2[0]);
            if (round1 != round2) return Integer.compare(round1, round2);
            return Integer.compare(Integer.parseInt(spot1[1]), Integer.parseInt(spot2[1]));
        });
        return allPlayers;
    }
    
    // Get team roster for a specific draft
    public List<Map<String, Object>> getTeamRoster(int draftId) {
        List<Map<String, Object>> teamRoster = new ArrayList<>();
        String sql = "SELECT t.team_name, t.user_team, p.full_name, p.position, p.avg_adp, p.predicted_score, dp.pick_number " +
                    "FROM teams t " +
                    "JOIN draft_picks dp ON t.draft_id = dp.draft_id AND t.team_name = dp.team_name " +
                    "JOIN players p ON dp.player_id = p.player_id " +
                    "WHERE t.draft_id = ? " +
                    "ORDER BY t.team_name, p.position, dp.pick_number";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, draftId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> player = new HashMap<>();
                player.put("team_name", rs.getString("team_name"));
                player.put("user_team", rs.getBoolean("user_team"));
                player.put("player_name", rs.getString("full_name"));
                player.put("position", rs.getString("position"));
                player.put("adp", rs.getDouble("avg_adp"));
                player.put("predicted_score", rs.getDouble("predicted_score"));
                player.put("draft_pick", rs.getInt("pick_number"));
                teamRoster.add(player);
            }
            Logger.logDraft(draftId, "SYSTEM", "TEAM_ROSTER_LOADED", "Loaded team roster with " + teamRoster.size() + " players");
        } catch (SQLException e) {
            Logger.logError("Failed to load team roster for draft " + draftId + ": " + e.getMessage());
        }
        return teamRoster;
    }
    
    // Get the most recent completed draft ID for a user
    public int getMostRecentCompletedDraftId(String username) {
        String sql = "SELECT draft_id FROM drafts WHERE username = ? AND complete_status = 1 ORDER BY draft_id DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("draft_id");
            }
        } catch (SQLException e) {
            Logger.logError("Failed to get most recent completed draft ID for user " + username + ": " + e.getMessage());
        }
        return -1;
    }
    
    // Get draft size for a specific draft
    public int getDraftSize(int draftId) {
        String sql = "SELECT num_teams FROM drafts WHERE draft_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, draftId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("num_teams");
            }
        } catch (SQLException e) {
            Logger.logError("Failed to get draft size for draft " + draftId + ": " + e.getMessage());
        }
        return -1;
    }
    
    // Create team array from database
    public List<Integer> createTeamIntArrayFromDB(int draftId, int teamIndex) {
        List<Integer> teamArray = new ArrayList<>();
        String sql = "SELECT team_array FROM teams WHERE draft_id = ? AND draft_spot = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, draftId);
            stmt.setInt(2, teamIndex);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String teamArrayStr = rs.getString("team_array");
                if (teamArrayStr != null && !teamArrayStr.isEmpty()) {
                    String[] playerIds = teamArrayStr.split(",");
                    for (String playerId : playerIds) {
                        if (!playerId.trim().isEmpty()) {
                            teamArray.add(Integer.parseInt(playerId.trim()));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            Logger.logError("Failed to get team array for draft " + draftId + " team " + teamIndex + ": " + e.getMessage());
        }
        return teamArray;
    }
    
    // EXACT COPY from WebApp
    public List<PlayerDataObject> createPlayerModelListFromIntList(List<Integer> teamArrayInt, int draftSize) {
        List<PlayerDataObject> playerModelList = new ArrayList<>();
        for(int playerInt : teamArrayInt) {
            PlayerDataObject player = this.createPlayerModel(playerInt, draftSize);
            playerModelList.add(player);
        }
        return playerModelList;
    }

    // EXACT COPY from WebApp
    private PlayerDataObject createPlayerModel(int pRank, int draftSize) {
        String sql = "SELECT * FROM players WHERE player_rank = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, pRank);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                String position = rs.getString("position");
                int playerRank = rs.getInt("player_rank");
                double predictedScore = rs.getBigDecimal("predicted_score").doubleValue();
                
                // Calculate ADP exactly like WebApp
                String avgADP;
                int round;
                if(playerRank <= draftSize) round = 1;
                else round = (playerRank / draftSize) + 1;
                int pick = playerRank % draftSize;
                if (pick == 0) {
                    pick = 1;
                }
                String pickStr = Integer.toString(pick);    
                if (Integer.toString(pick).length() == 1) {
                    pickStr = "0" + pick;
                }
                avgADP = round + "." + pickStr;
                
                // Use WebApp constructor: (name, position, playerRank, predictedScore, avgADP)
                return new PlayerDataObject(name, position, (double)playerRank, predictedScore, Double.parseDouble(avgADP));
            }
        } catch (SQLException e) {
            Logger.logError("Failed to create player model for rank " + pRank + ": " + e.getMessage());
        }
        return null;
    }
    
    // Get team name from draft spot
    public String getUserTeamNameFromDraftSpot(int draftId, int teamIndex) {
        String sql = "SELECT team_name FROM teams WHERE draft_id = ? AND draft_spot = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, draftId);
            stmt.setInt(2, teamIndex);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("team_name");
            }
        } catch (SQLException e) {
            Logger.logError("Failed to get team name for draft " + draftId + " team " + teamIndex + ": " + e.getMessage());
        }
        return "Team " + teamIndex;
    }
    
    // Get team roster for a specific draft and team
    public List<Map<String, Object>> getTeamRoster(int draftId, int teamIndex) {
        List<Map<String, Object>> teamRoster = new ArrayList<>();
        
        // Get team players
        List<Integer> teamArrayInt = createTeamIntArrayFromDB(draftId, teamIndex);
        List<PlayerDataObject> teamPlayerList = createPlayerModelListFromIntList(teamArrayInt, getDraftSize(draftId));
        String teamName = getUserTeamNameFromDraftSpot(draftId, teamIndex);
        
        // Use shared utility to build roster
        try {
            // Import the shared utility class
            Class<?> draftAnalysisClass = Class.forName("main.java.com.example.common.DraftAnalysisUtils");
            Object roster = draftAnalysisClass.getMethod("buildTeamRoster", String.class, boolean.class, List.class)
                .invoke(null, teamName, false, teamPlayerList);
            
            // Convert roster to map format for console display
            // This is a simplified version - in a full implementation you'd use reflection to access the roster data
            
            // For now, just return the raw player data
            for (PlayerDataObject player : teamPlayerList) {
                Map<String, Object> playerData = new HashMap<>();
                playerData.put("player_name", player.getFullName());
                playerData.put("position", player.getPosition());
                playerData.put("adp", player.getAvgADP());
                playerData.put("predicted_score", player.getPredictedScore());
                playerData.put("draft_pick", (int) Math.round(player.getAvgADP()));
                teamRoster.add(playerData);
            }
            
        } catch (Exception e) {
            Logger.logError("Failed to build team roster using shared utilities: " + e.getMessage());
            // Fallback to simple player list
            for (PlayerDataObject player : teamPlayerList) {
                Map<String, Object> playerData = new HashMap<>();
                playerData.put("player_name", player.getFullName());
                playerData.put("position", player.getPosition());
                playerData.put("adp", player.getAvgADP());
                playerData.put("predicted_score", player.getPredictedScore());
                playerData.put("draft_pick", (int) Math.round(player.getAvgADP()));
                teamRoster.add(playerData);
            }
        }
        
        return teamRoster;
    }
} 