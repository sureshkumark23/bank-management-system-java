package com.bankapp.dao;

import com.bankapp.model.Transaction;
import java.util.List;

public interface TransactionDAO {

    // Record a new transaction
    boolean addTransaction(Transaction transaction);

    // Get all transactions for an account
    List<Transaction> getTransactionsByAccountId(int accountId);

    // Get last N transactions
    List<Transaction> getLastNTransactions(int accountId, int n);
}