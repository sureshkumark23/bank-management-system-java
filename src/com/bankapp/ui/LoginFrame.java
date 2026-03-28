package com.bankapp.ui;

import com.bankapp.dao.UserDAO;
import com.bankapp.dao.impl.UserDAOImpl;
import com.bankapp.model.User;

import javax.swing.*;
import java.awt.*;
public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel statusLabel;

    public LoginFrame() {
        setTitle("Bank Management System — Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // center on screen
        setResizable(false);

        initComponents();
    }

    private void initComponents() {

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Title
        JLabel titleLabel = new JLabel("🏦 Bank Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        // Username label
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(new JLabel("Username:"), gbc);

        // Username field
        usernameField = new JTextField(15);
        gbc.gridx = 1; gbc.gridy = 1;
        mainPanel.add(usernameField, gbc);

        // Password label
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(new JLabel("Password:"), gbc);

        // Password field
        passwordField = new JPasswordField(15);
        gbc.gridx = 1; gbc.gridy = 2;
        mainPanel.add(passwordField, gbc);

        // Login button
        loginButton = new JButton("Login");
        loginButton.setBackground(new Color(0, 102, 204));
loginButton.setForeground(Color.WHITE);
loginButton.setFocusPainted(false);
loginButton.setOpaque(true);
loginButton.setBorderPainted(false);
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        mainPanel.add(loginButton, gbc);

        // Status label
        statusLabel = new JLabel("");
        statusLabel.setForeground(Color.RED);
        gbc.gridx = 0; gbc.gridy = 4;
        mainPanel.add(statusLabel, gbc);

        // Login button action
        loginButton.addActionListener(e -> handleLogin());

        add(mainPanel);
    }
    private void handleLogin() {
    String username = usernameField.getText().trim();
    String password = new String(passwordField.getPassword()).trim();

    if (username.isEmpty() || password.isEmpty()) {
        statusLabel.setText("Please enter username and password!");
        return;
    }

    // Use UserDAO instead of direct SQL
    UserDAO userDAO = new UserDAOImpl();
    User user = userDAO.login(username, password);

    if (user != null) {
        statusLabel.setForeground(Color.GREEN);
        statusLabel.setText("✅ Login successful!");
        new DashboardFrame(user.getUserId(), user.getUsername(), user.getRole()).setVisible(true);
        this.dispose();
    } else {
        statusLabel.setForeground(Color.RED);
        statusLabel.setText("❌ Invalid username or password!");
    }
}

    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}