package com.bankapp.exception;

public class AccountLockedException extends BankException {

    public AccountLockedException() {
        super("Account is locked! Please contact the bank.");
    }
}