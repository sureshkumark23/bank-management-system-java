package com.bankapp.model;

import java.time.LocalDateTime;

public class Transaction {

    private int txnId;
    private int accountId;
    private String txnType;      // DEPOSIT, WITHDRAW, TRANSFER
    private double amount;
    private double balanceAfter;
    private String description;
    private LocalDateTime txnDate;

    // Constructor
    public Transaction(int txnId, int accountId, String txnType,
                       double amount, double balanceAfter,
                       String description, LocalDateTime txnDate) {
        this.txnId = txnId;
        this.accountId = accountId;
        this.txnType = txnType;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.description = description;
        this.txnDate = txnDate;
    }

    // Getters
    public int getTxnId()            { return txnId; }
    public int getAccountId()        { return accountId; }
    public String getTxnType()       { return txnType; }
    public double getAmount()        { return amount; }
    public double getBalanceAfter()  { return balanceAfter; }
    public String getDescription()   { return description; }
    public LocalDateTime getTxnDate(){ return txnDate; }

    @Override
    public String toString() {
        return "Transaction[id=" + txnId + ", type=" + txnType +
               ", amount=" + amount + ", date=" + txnDate + "]";
    }
}