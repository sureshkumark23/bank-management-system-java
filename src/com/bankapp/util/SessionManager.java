package com.bankapp.util;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SessionManager {

    private static final int TIMEOUT_MINUTES = 5;

    // HashMap to track active sessions → username = session start time
    private static HashMap<String, Long> activeSessions = new HashMap<>();

    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> timeoutTask;
    private JFrame currentFrame;
    private String username;

    public SessionManager(JFrame frame, String username) {
        this.currentFrame = frame;
        this.username     = username;
        this.scheduler    = Executors.newSingleThreadScheduledExecutor();
    }

    // Start the session timer
    public void startSession() {

        // Add to active sessions map
        activeSessions.put(username, System.currentTimeMillis());
        System.out.println("✅ Session started for: " + username);
        System.out.println("🗂️  Active sessions: " + getActiveSessions());

        timeoutTask = scheduler.schedule(() -> {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(
                    currentFrame,
                    "⏰ Session expired due to inactivity!\nPlease login again.",
                    "Session Timeout",
                    JOptionPane.WARNING_MESSAGE
                );
                endSession();
                currentFrame.dispose();
                new com.bankapp.ui.LoginFrame().setVisible(true);
            });
        }, TIMEOUT_MINUTES, TimeUnit.MINUTES);
    }

    // Reset timer on any user activity
    public void resetTimer() {
        if (timeoutTask != null) {
            timeoutTask.cancel(false);
        }
        // Update last activity time in map
        activeSessions.put(username, System.currentTimeMillis());
        startSession();
    }

    // End session — remove from map
    public void endSession() {
        activeSessions.remove(username);
        if (timeoutTask != null) {
            timeoutTask.cancel(false);
        }
        scheduler.shutdown();
        System.out.println("✅ Session ended for: " + username);
        System.out.println("🗂️  Active sessions: " + getActiveSessions());
    }

    // Get all active sessions — useful for admin panel
    public static Map<String, Long> getActiveSessions() {
        return activeSessions;
    }

    // Check if a user is already logged in
    public static boolean isSessionActive(String username) {
        return activeSessions.containsKey(username);
    }
}