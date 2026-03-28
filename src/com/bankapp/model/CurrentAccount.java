package com.bankapp.model;

public class CurrentAccount extends Account {

    private static final double INTEREST_RATE = 0.0; // 0% for current accounts

    public CurrentAccount(int accountId, int userId, double balance, boolean isActive) {
        super(accountId, userId, "CURRENT", balance, isActive);
    }

    @Override
    public double calculateInterest() {
        return getBalance() * INTEREST_RATE / 100;
    }
}
