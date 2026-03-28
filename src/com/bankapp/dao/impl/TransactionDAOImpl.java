package com.bankapp.dao.impl;

import com.bankapp.dao.TransactionDAO;
import com.bankapp.model.Transaction;
import com.bankapp.util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAOImpl implements TransactionDAO {

    private Connection con;

    public TransactionDAOImpl() {
        this.con = DBConnection.getInstance().getConnection();
    }

    @Override
    public boolean addTransaction(Transaction transaction) {
        String sql = "INSERT INTO transactions (account_id, txn_type, amount, balance_after, description) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, transaction.getAccountId());
            ps.setString(2, transaction.getTxnType());
            ps.setDouble(3, transaction.getAmount());
            ps.setDouble(4, transaction.getBalanceAfter());
            ps.setString(5, transaction.getDescription());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("❌ addTransaction failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Transaction> getTransactionsByAccountId(int accountId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE account_id = ? ORDER BY txn_date DESC";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, accountId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                transactions.add(buildTransaction(rs));
            }
        } catch (SQLException e) {
            System.out.println("❌ getTransactions failed: " + e.getMessage());
        }
        return transactions;
    }

    @Override
    public List<Transaction> getLastNTransactions(int accountId, int n) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE account_id = ? " +
                     "ORDER BY txn_date DESC LIMIT ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, accountId);
            ps.setInt(2, n);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                transactions.add(buildTransaction(rs));
            }
        } catch (SQLException e) {
            System.out.println("❌ getLastNTransactions failed: " + e.getMessage());
        }
        return transactions;
    }

    // Helper method — builds Transaction object from ResultSet
    private Transaction buildTransaction(ResultSet rs) throws SQLException {
        return new Transaction(
            rs.getInt("txn_id"),
            rs.getInt("account_id"),
            rs.getString("txn_type"),
            rs.getDouble("amount"),
            rs.getDouble("balance_after"),
            rs.getString("description"),
            rs.getTimestamp("txn_date").toLocalDateTime()
        );
    }
}