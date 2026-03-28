package com.bankapp.ui;

import com.bankapp.model.Account;
import com.bankapp.model.Transaction;
import com.bankapp.service.AccountService;
import com.bankapp.util.SessionManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DashboardFrame extends JFrame {

    private int userId;
    private String username;
    private String role;

    private AccountService accountService;
    private Account account;

    private JLabel balanceLabel;
    private JTextField amountField;
    private JTable transactionTable;
    private DefaultTableModel tableModel;
    private SessionManager sessionManager;

    public DashboardFrame(int userId, String username, String role) {
        this.userId         = userId;
        this.username       = username;
        this.role           = role;
        this.accountService = new AccountService();
        this.account        = accountService.getAccount(userId);

        setTitle("Bank Management System — Dashboard");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();
        loadBalance();
        loadTransactions();

        // Start session timeout timer
        sessionManager = new SessionManager(this, username);
        sessionManager.startSession();
    }

    private void initComponents() {

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // ── Top Panel ──
        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        JLabel welcomeLabel = new JLabel("Welcome, " + username + " (" + role + ")");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        balanceLabel = new JLabel("Balance: Loading...");
        balanceLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        topPanel.add(welcomeLabel);
        topPanel.add(balanceLabel);

        // ── Action Panel ──
        JPanel actionPanel = new JPanel(new FlowLayout());
        actionPanel.setBorder(BorderFactory.createTitledBorder("Actions"));

        amountField         = new JTextField(10);
        JButton depositBtn  = new JButton("Deposit");
        JButton withdrawBtn = new JButton("Withdraw");
        JButton refreshBtn  = new JButton("Refresh");
        JButton exportBtn   = new JButton("Export Statement");
        JButton registerBtn = new JButton("New Customer");

        depositBtn.setBackground(new Color(0, 153, 0));
        depositBtn.setForeground(Color.WHITE);
        depositBtn.setOpaque(true);
        depositBtn.setBorderPainted(false);

        withdrawBtn.setBackground(new Color(204, 0, 0));
        withdrawBtn.setForeground(Color.WHITE);
        withdrawBtn.setOpaque(true);
        withdrawBtn.setBorderPainted(false);

        refreshBtn.setBackground(new Color(0, 102, 204));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setOpaque(true);
        refreshBtn.setBorderPainted(false);

        exportBtn.setBackground(new Color(102, 0, 153));
        exportBtn.setForeground(Color.WHITE);
        exportBtn.setOpaque(true);
        exportBtn.setBorderPainted(false);

        registerBtn.setBackground(new Color(153, 76, 0));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setOpaque(true);
        registerBtn.setBorderPainted(false);

        actionPanel.add(new JLabel("Amount: "));
        actionPanel.add(amountField);
        actionPanel.add(depositBtn);
        actionPanel.add(withdrawBtn);
        actionPanel.add(refreshBtn);
        actionPanel.add(exportBtn);

        if (role.equals("ADMIN")) {
            actionPanel.add(registerBtn);
        }

        // ── Transaction Table ──
        String[] columns = {"Txn ID", "Type", "Amount", "Balance After", "Date"};
        tableModel = new DefaultTableModel(columns, 0);
        transactionTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Recent Transactions"));

        // ── Button Actions ──
        depositBtn.addActionListener(e -> {
            sessionManager.resetTimer();
            handleDeposit();
        });
        withdrawBtn.addActionListener(e -> {
            sessionManager.resetTimer();
            handleWithdraw();
        });
        refreshBtn.addActionListener(e -> {
            sessionManager.resetTimer();
            loadBalance();
            loadTransactions();
        });
        exportBtn.addActionListener(e -> {
            sessionManager.resetTimer();
            exportStatement();
        });
        registerBtn.addActionListener(e -> {
            sessionManager.resetTimer();
            new RegisterFrame().setVisible(true);
        });

        JPanel topAndAction = new JPanel(new GridLayout(2, 1));
        topAndAction.add(topPanel);
        topAndAction.add(actionPanel);

        mainPanel.add(topAndAction, BorderLayout.NORTH);
        mainPanel.add(scrollPane,   BorderLayout.CENTER);

        add(mainPanel);
    }

    private void loadBalance() {
        if (account != null) {
            double balance = accountService.getBalance(account.getAccountId());
            balanceLabel.setText("Balance: ₹" + String.format("%.2f", balance));
        } else {
            balanceLabel.setText("No account found!");
        }
    }

    // ✅ SwingWorker — loads transactions in background thread
    // UI stays responsive while DB fetches data
    private void loadTransactions() {
        tableModel.setRowCount(0);
        if (account == null) return;

        SwingWorker<List<Transaction>, Void> worker = new SwingWorker<>() {

            @Override
            protected List<Transaction> doInBackground() throws Exception {
                // Runs on BACKGROUND thread — heavy DB work here
                return accountService.getRecentTransactions(account.getAccountId());
            }

            @Override
            protected void done() {
                // Runs back on UI thread — safe to update UI here
                try {
                    List<Transaction> transactions = get();
                    for (Transaction t : transactions) {
                        tableModel.addRow(new Object[]{
                            t.getTxnId(),
                            t.getTxnType(),
                            "₹" + t.getAmount(),
                            "₹" + t.getBalanceAfter(),
                            t.getTxnDate()
                        });
                    }
                    loadBalance();
                } catch (Exception e) {
                    System.out.println("❌ SwingWorker error: " + e.getMessage());
                }
            }
        };

        worker.execute(); // kick off background thread
    }

    private void handleDeposit() {
        try {
            double amount   = Double.parseDouble(amountField.getText().trim());
            boolean success = accountService.deposit(account.getAccountId(), amount);
            if (success) {
                JOptionPane.showMessageDialog(this, "✅ Deposit Successful!");
                amountField.setText("");
                loadBalance();
                loadTransactions();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "❌ Please enter a valid amount!");
        }
    }

    private void handleWithdraw() {
        try {
            double amount   = Double.parseDouble(amountField.getText().trim());
            boolean success = accountService.withdraw(account.getAccountId(), amount);
            if (success) {
                JOptionPane.showMessageDialog(this, "✅ Withdrawal Successful!");
                amountField.setText("");
                loadBalance();
                loadTransactions();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "❌ Please enter a valid amount!");
        }
    }

    private void exportStatement() {
        if (account == null) return;

        List<Transaction> all = accountService.getAllTransactions(account.getAccountId());
        String fileName = "statement_" + username + "_" +
                          java.time.LocalDate.now() + ".txt";

        try (java.io.BufferedWriter writer =
                new java.io.BufferedWriter(new java.io.FileWriter(fileName))) {

            writer.write("========================================");
            writer.newLine();
            writer.write("   BANK MANAGEMENT SYSTEM");
            writer.newLine();
            writer.write("   Account Statement");
            writer.newLine();
            writer.write("========================================");
            writer.newLine();
            writer.write("Customer : " + username);
            writer.newLine();
            writer.write("Date     : " + java.time.LocalDate.now());
            writer.newLine();
            writer.write("----------------------------------------");
            writer.newLine();
            writer.write(String.format("%-8s %-10s %-10s %-12s %s",
                "TxnID", "Type", "Amount", "Balance", "Date"));
            writer.newLine();
            writer.write("----------------------------------------");
            writer.newLine();

            for (Transaction t : all) {
                writer.write(String.format("%-8d %-10s %-10.2f %-12.2f %s",
                    t.getTxnId(),
                    t.getTxnType(),
                    t.getAmount(),
                    t.getBalanceAfter(),
                    t.getTxnDate()
                ));
                writer.newLine();
            }

            writer.write("========================================");
            writer.newLine();
            writer.write("Current Balance: ₹" +
                String.format("%.2f", accountService.getBalance(account.getAccountId())));
            writer.newLine();
            writer.write("========================================");

            JOptionPane.showMessageDialog(this,
                "✅ Statement exported!\nFile: " + fileName);

        } catch (java.io.IOException e) {
            JOptionPane.showMessageDialog(this,
                "❌ Export failed: " + e.getMessage());
        }
    }
}