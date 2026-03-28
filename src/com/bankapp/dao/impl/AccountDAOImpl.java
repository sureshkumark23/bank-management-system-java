package com.bankapp.dao.impl;

import com.bankapp.dao.AccountDAO;
import com.bankapp.exception.AccountLockedException;
import com.bankapp.exception.InsufficientFundsException;
import com.bankapp.model.Account;
import com.bankapp.model.SavingsAccount;
import com.bankapp.model.CurrentAccount;
import com.bankapp.util.DBConnection;

import java.sql.*;
import java.util.List;

public class AccountDAOImpl implements AccountDAO {

    private Connection con;

    public AccountDAOImpl() {
        this.con = DBConnection.getInstance().getConnection();
    }

    @Override
    public boolean createAccount(Account account) {
        String sql = "INSERT INTO accounts (user_id, account_type, balance, is_active) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, account.getUserId());
            ps.setString(2, account.getAccountType());
            ps.setDouble(3, account.getBalance());
            ps.setBoolean(4, account.isActive());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("❌ createAccount failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Account getAccountById(int accountId) {
        String sql = "SELECT * FROM accounts WHERE account_id = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, accountId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return buildAccount(rs);
            }
        } catch (SQLException e) {
            System.out.println("❌ getAccountById failed: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Account getAccountByUserId(int userId) {
        String sql = "SELECT * FROM accounts WHERE user_id = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return buildAccount(rs);
            }
        } catch (SQLException e) {
            System.out.println("❌ getAccountByUserId failed: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean deposit(int accountId, double amount) {
        String sql = "UPDATE accounts SET balance = balance + ? WHERE account_id = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setDouble(1, amount);
            ps.setInt(2, accountId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("❌ deposit failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean withdraw(int accountId, double amount)
            throws InsufficientFundsException, AccountLockedException {

        // Step 1 — fetch current account
        Account account = getAccountById(accountId);

        // Step 2 — check if account is locked
        if (!account.isActive()) {
            throw new AccountLockedException();
        }

        // Step 3 — check if balance is enough
        if (account.getBalance() < amount) {
            throw new InsufficientFundsException(amount);
        }

        // Step 4 — all good, do withdrawal with transaction
        String sql = "UPDATE accounts SET balance = balance - ? WHERE account_id = ?";
        try {
            con.setAutoCommit(false); // ← start transaction
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setDouble(1, amount);
            ps.setInt(2, accountId);
            ps.executeUpdate();
            con.commit();            // ← save to DB
            return true;
        } catch (SQLException e) {
            try {
                con.rollback();      // ← undo if anything fails
            } catch (SQLException ex) {
                System.out.println("❌ Rollback failed: " + ex.getMessage());
            }
            return false;
        } finally {
            try {
                con.setAutoCommit(true); // ← reset back to normal
            } catch (SQLException e) {
                System.out.println("❌ AutoCommit reset failed: " + e.getMessage());
            }
        }
    }

    @Override
    public double getBalance(int accountId) {
        String sql = "SELECT balance FROM accounts WHERE account_id = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, accountId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("balance");
            }
        } catch (SQLException e) {
            System.out.println("❌ getBalance failed: " + e.getMessage());
        }
        return 0.0;
    }

    @Override
    public boolean updateAccountStatus(int accountId, boolean isActive) {
        String sql = "UPDATE accounts SET is_active = ? WHERE account_id = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setBoolean(1, isActive);
            ps.setInt(2, accountId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("❌ updateAccountStatus failed: " + e.getMessage());
            return false;
        }
    }

    // Helper method — builds Account object from ResultSet
    private Account buildAccount(ResultSet rs) throws SQLException {
        int accountId  = rs.getInt("account_id");
        int userId     = rs.getInt("user_id");
        String type    = rs.getString("account_type");
        double balance = rs.getDouble("balance");
        boolean active = rs.getBoolean("is_active");

        if (type.equals("SAVINGS")) {
            return new SavingsAccount(accountId, userId, balance, active);
        } else {
            return new CurrentAccount(accountId, userId, balance, active);
        }
    }
}