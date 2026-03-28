package com.bankapp.model;

public class SavingsAccount extends Account {

    private static final double INTEREST_RATE = 4.0; // 4% per year

    public SavingsAccount(int accountId, int userId, double balance, boolean isActive) {
        super(accountId, userId, "SAVINGS", balance, isActive);
    }

    @Override
    public double calculateInterest() {
        return getBalance() * INTEREST_RATE / 100;
    }
}