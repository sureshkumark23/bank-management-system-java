package com.bankapp.dao;

import com.bankapp.exception.AccountLockedException;
import com.bankapp.exception.InsufficientFundsException;
import com.bankapp.model.Account;

import java.util.List;

public interface AccountDAO {

    // Create new account in DB
    boolean createAccount(Account account);

    // Get account by account ID
    Account getAccountById(int accountId);

    // Get account by user ID
    Account getAccountByUserId(int userId);

    // Deposit money
    boolean deposit(int accountId, double amount);

    // Withdraw money
    boolean withdraw(int accountId, double amount)
        throws InsufficientFundsException, AccountLockedException;

    // Get balance
    double getBalance(int accountId);

    // Update account status (lock/unlock)
    boolean updateAccountStatus(int accountId, boolean isActive);
}