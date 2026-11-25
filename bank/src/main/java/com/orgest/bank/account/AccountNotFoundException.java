package com.orgest.bank.account;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(String id) {
        super("Account not found: " + id);
    }
}