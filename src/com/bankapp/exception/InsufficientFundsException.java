package com.bankapp.exception;

public class InsufficientFundsException extends BankException {

    private double amount;

    public InsufficientFundsException(double amount) {
        super("Insufficient funds! Tried to withdraw: " + amount);
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }
}