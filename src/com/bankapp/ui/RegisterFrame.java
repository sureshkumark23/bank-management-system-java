package com.bankapp.ui;

import com.bankapp.dao.UserDAO;
import com.bankapp.dao.impl.UserDAOImpl;
import com.bankapp.dao.impl.AccountDAOImpl;
import com.bankapp.model.User;
import com.bankapp.model.SavingsAccount;
import com.bankapp.model.CurrentAccount;

import javax.swing.*;
import java.awt.*;

public class RegisterFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> accountTypeBox;
    private JTextField initialBalanceField;
    private JLabel statusLabel;

    public RegisterFrame() {
        setTitle("Bank Management System — Register Customer");
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Title
        JLabel titleLabel = new JLabel("Register New Customer");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        // Username
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(new JLabel("Username:"), gbc);
        usernameField = new JTextField(15);
        gbc.gridx = 1; gbc.gridy = 1;
        mainPanel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(new JLabel("Password:"), gbc);
        passwordField = new JPasswordField(15);
        gbc.gridx = 1; gbc.gridy = 2;
        mainPanel.add(passwordField, gbc);

        // Account Type
        gbc.gridx = 0; gbc.gridy = 3;
        mainPanel.add(new JLabel("Account Type:"), gbc);
        accountTypeBox = new JComboBox<>(new String[]{"SAVINGS", "CURRENT"});
        gbc.gridx = 1; gbc.gridy = 3;
        mainPanel.add(accountTypeBox, gbc);

        // Initial Balance
        gbc.gridx = 0; gbc.gridy = 4;
        mainPanel.add(new JLabel("Initial Balance:"), gbc);
        initialBalanceField = new JTextField(15);
        gbc.gridx = 1; gbc.gridy = 4;
        mainPanel.add(initialBalanceField, gbc);

        // Register Button
        JButton registerBtn = new JButton("Register");
        registerBtn.setBackground(new Color(0, 153, 0));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setOpaque(true);
        registerBtn.setBorderPainted(false);
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        mainPanel.add(registerBtn, gbc);

        // Status Label
        statusLabel = new JLabel("");
        gbc.gridx = 0; gbc.gridy = 6;
        mainPanel.add(statusLabel, gbc);

        // Register button action
        registerBtn.addActionListener(e -> handleRegister());

        add(mainPanel);
    }

    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String accountType = (String) accountTypeBox.getSelectedItem();
        String balanceText = initialBalanceField.getText().trim();

        // Validation
        if (username.isEmpty() || password.isEmpty() || balanceText.isEmpty()) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("❌ All fields are required!");
            return;
        }

        double initialBalance;
        try {
            initialBalance = Double.parseDouble(balanceText);
            if (initialBalance < 0) {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("❌ Balance cannot be negative!");
                return;
            }
        } catch (NumberFormatException e) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("❌ Invalid balance amount!");
            return;
        }

        UserDAO userDAO = new UserDAOImpl();

        // Check duplicate username
        if (userDAO.usernameExists(username)) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("❌ Username already exists!");
            return;
        }

        // Create user
        User newUser = new User(0, username, password, "CUSTOMER");
        boolean userCreated = userDAO.createUser(newUser);

        if (userCreated) {
            // Get the newly created user to get their ID
            User createdUser = userDAO.login(username, password);

            // Create account for this user
            AccountDAOImpl accountDAO = new AccountDAOImpl();
            boolean accountCreated;

            if (accountType.equals("SAVINGS")) {
                accountCreated = accountDAO.createAccount(
                    new SavingsAccount(0, createdUser.getUserId(), initialBalance, true)
                );
            } else {
                accountCreated = accountDAO.createAccount(
                    new CurrentAccount(0, createdUser.getUserId(), initialBalance, true)
                );
            }

            if (accountCreated) {
                statusLabel.setForeground(Color.GREEN);
                statusLabel.setText("✅ Customer registered successfully!");
                // Clear fields
                usernameField.setText("");
                passwordField.setText("");
                initialBalanceField.setText("");
            }
        } else {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("❌ Registration failed!");
        }
    }
}