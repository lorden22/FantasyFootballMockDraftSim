package com.example.Mock.DAO;

import java.sql.*;
import java.util.*;
import com.example.common.Logger;
import com.example.common.PlayerDataObject;

public class PlayerDataObjectRepository {
    public static List<PlayerDataObject> getAllPlayers() {
        Logger.logInfo("Loading all players from database");
        List<PlayerDataObject> players = new ArrayList<>();
        String sql = "SELECT name, position, player_rank, predicted_score FROM players";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String name = rs.getString("name");
                String position = rs.getString("position");
                double playerRank = rs.getDouble("player_rank");
                double predictedScore = rs.getDouble("predicted_score");
                double avgADP = playerRank; // Adjust if you have a separate ADP column
                players.add(new PlayerDataObject(name, position, playerRank, predictedScore, avgADP));
            }
            Logger.logInfo("Successfully loaded " + players.size() + " players from database");
        } catch (SQLException e) {
            Logger.logError("Failed to load players from database: " + e.getMessage());
        }
        return players;
    }
} 