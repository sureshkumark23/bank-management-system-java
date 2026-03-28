package com.bankapp.model;

public abstract class Account {

    private int accountId;
    private int userId;
    private String accountType;
    private double balance;
    private boolean isActive;

    // Constructor
    public Account(int accountId, int userId, String accountType, double balance, boolean isActive) {
        this.accountId = accountId;
        this.userId = userId;
        this.accountType = accountType;
        this.balance = balance;
        this.isActive = isActive;
    }

    // Abstract method — every child class MUST implement this
    public abstract double calculateInterest();

    // Getters
    public int getAccountId()      { return accountId; }
    public int getUserId()         { return userId; }
    public String getAccountType() { return accountType; }
    public double getBalance()     { return balance; }
    public boolean isActive()      { return isActive; }

    // Setters
    public void setBalance(double balance)     { this.balance = balance; }
    public void setActive(boolean isActive)    { this.isActive = isActive; }

    @Override
    public String toString() {
        return "Account[id=" + accountId + ", type=" + accountType + ", balance=" + balance + "]";
    }
}