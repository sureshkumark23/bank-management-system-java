package com.bankapp.service;

import com.bankapp.dao.AccountDAO;
import com.bankapp.dao.TransactionDAO;
import com.bankapp.dao.impl.AccountDAOImpl;
import com.bankapp.dao.impl.TransactionDAOImpl;
import com.bankapp.exception.AccountLockedException;
import com.bankapp.exception.InsufficientFundsException;
import com.bankapp.model.Account;
import com.bankapp.model.Transaction;

import java.time.LocalDateTime;
import java.util.List;

public class AccountService {

    private AccountDAO accountDAO;
    private TransactionDAO transactionDAO;

    public AccountService() {
        this.accountDAO     = new AccountDAOImpl();
        this.transactionDAO = new TransactionDAOImpl();
    }

    // Deposit money + record transaction
    public boolean deposit(int accountId, double amount) {

        // Business rule — amount must be positive
        if (amount <= 0) {
            System.out.println("❌ Deposit amount must be positive!");
            return false;
        }

        boolean success = accountDAO.deposit(accountId, amount);

        if (success) {
            // Record the transaction
            double newBalance = accountDAO.getBalance(accountId);
            Transaction txn = new Transaction(
                0, accountId, "DEPOSIT",
                amount, newBalance,
                "Deposit of " + amount,
                LocalDateTime.now()
            );
            transactionDAO.addTransaction(txn);
            System.out.println("✅ Deposit successful!");
        }
        return success;
    }

    // Withdraw money + record transaction
    public boolean withdraw(int accountId, double amount) {

        // Business rule — amount must be positive
        if (amount <= 0) {
            System.out.println("❌ Withdrawal amount must be positive!");
            return false;
        }

        try {
            boolean success = accountDAO.withdraw(accountId, amount);

            if (success) {
                // Record the transaction
                double newBalance = accountDAO.getBalance(accountId);
                Transaction txn = new Transaction(
                    0, accountId, "WITHDRAW",
                    amount, newBalance,
                    "Withdrawal of " + amount,
                    LocalDateTime.now()
                );
                transactionDAO.addTransaction(txn);
                System.out.println("✅ Withdrawal successful!");
            }
            return success;

        } catch (InsufficientFundsException e) {
            System.out.println("❌ " + e.getMessage());
            return false;
        } catch (AccountLockedException e) {
            System.out.println("❌ " + e.getMessage());
            return false;
        }
    }

    // Get account details
    public Account getAccount(int accountId) {
        return accountDAO.getAccountById(accountId);
    }

    // Get balance
    public double getBalance(int accountId) {
        return accountDAO.getBalance(accountId);
    }

    // Get last 5 transactions
    public List<Transaction> getRecentTransactions(int accountId) {
        return transactionDAO.getLastNTransactions(accountId, 5);
    }

    // Get all transactions
    public List<Transaction> getAllTransactions(int accountId) {
        return transactionDAO.getTransactionsByAccountId(accountId);
    }
}
