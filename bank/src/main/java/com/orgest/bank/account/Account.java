package com.orgest.bank.account;

import java.math.BigDecimal;
import java.time.Instant;                // NEW
import java.util.ArrayList;              // NEW
import java.util.List;                   // NEW

public class Account {

    private final String id;
    private final String owner;
    private BigDecimal balance;
    private final List<Transaction> transactions = new ArrayList<>();   // NEW

    public Account(String id, String owner) {
        this.id = id;
        this.owner = owner;
        this.balance = BigDecimal.ZERO;
    }

    public String getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public List<Transaction> getTransactions() {                        // NEW
        return List.copyOf(transactions);
    }

    public void deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        balance = balance.add(amount);
        recordTransaction(TransactionType.DEPOSIT, amount);             // NEW
    }

    public void withdraw(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdraw amount must be positive");
        }
        if (amount.compareTo(balance) > 0) {
            throw new InsufficientFundsException();
        }
        balance = balance.subtract(amount);
        recordTransaction(TransactionType.WITHDRAWAL, amount);          // NEW
    }

    private void recordTransaction(TransactionType type, BigDecimal amount) {   // NEW
        transactions.add(new Transaction(type, amount, balance, Instant.now()));
    }
}