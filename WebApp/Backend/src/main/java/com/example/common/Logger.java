package com.example.common;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private static final String LOG_DIR = "logs/";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    static {
        // Create logs directory if it doesn't exist
        File logDir = new File(LOG_DIR);
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
    }
    
    private static String getCurrentDate() {
        return LocalDateTime.now().format(DATE_FORMAT);
    }
    
    private static String getCurrentTimestamp() {
        return LocalDateTime.now().format(TIMESTAMP_FORMAT);
    }
    
    private static void writeToLog(String filename, String message) {
        try (FileWriter writer = new FileWriter(LOG_DIR + filename, true)) {
            writer.write(getCurrentTimestamp() + " - " + message + "\n");
        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + e.getMessage());
        }
    }
    
    public static void logAuth(String username, String action, String result) {
        String message = String.format("AUTH | Username: %s | Action: %s | Result: %s", username, action, result);
        writeToLog("auth_" + getCurrentDate() + ".log", message);
    }
    
    public static void logDraft(int draftId, String username, String action, String details) {
        String message = String.format("DRAFT | DraftID: %d | Username: %s | Action: %s | Details: %s", draftId, username, action, details);
        writeToLog("draft_" + getCurrentDate() + ".log", message);
    }
    
    public static void logDraftProgress(int draftId, int oldRound, int oldPick, int newRound, int newPick) {
        String message = String.format("DRAFT_PROGRESS | DraftID: %d | Old: Round %d Pick %d | New: Round %d Pick %d", draftId, oldRound, oldPick, newRound, newPick);
        writeToLog("draft_" + getCurrentDate() + ".log", message);
    }
    
    public static void logPlayerDrafted(int draftId, String username, String playerName, String position, int round, int pick) {
        String message = String.format("PLAYER_DRAFTED | DraftID: %d | Username: %s | Player: %s (%s) | Round: %d Pick: %d", draftId, username, playerName, position, round, pick);
        writeToLog("draft_" + getCurrentDate() + ".log", message);
    }
    
    public static void logTeamUpdate(int draftId, String username, String teamName, String action) {
        String message = String.format("TEAM_UPDATE | DraftID: %d | Username: %s | Team: %s | Action: %s", draftId, username, teamName, action);
        writeToLog("draft_" + getCurrentDate() + ".log", message);
    }
    
    public static void logInfo(String message) {
        writeToLog("application_" + getCurrentDate() + ".log", "INFO | " + message);
    }
    
    public static void logError(String message) {
        writeToLog("application_" + getCurrentDate() + ".log", "ERROR | " + message);
    }
    
    public static void logWarning(String message) {
        writeToLog("application_" + getCurrentDate() + ".log", "WARNING | " + message);
    }
    
    public static void logUserInteraction(String username, String action, String details) {
        String message = String.format("USER_INTERACTION | Username: %s | Action: %s | Details: %s", username, action, details);
        writeToLog("application_" + getCurrentDate() + ".log", message);
    }
} 