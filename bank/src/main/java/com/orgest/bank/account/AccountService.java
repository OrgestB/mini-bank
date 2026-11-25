package com.orgest.bank.account;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AccountService {

    private final Map<String, Account> accounts = new ConcurrentHashMap<>();   // CHANGED

    public Account openAccount(String owner) {
        String id = UUID.randomUUID().toString();
        Account account = new Account(id, owner);
        accounts.put(id, account);
        return account;
    }

    public Optional<Account> findAccount(String id) {
        return Optional.ofNullable(accounts.get(id));
    }

    public Account getAccount(String id) {
        return findAccount(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
    }

    public Account deposit(String id, BigDecimal amount) {
        Account account = getAccount(id);
        account.deposit(amount);
        return account;
    }

    public Account withdraw(String id, BigDecimal amount) {
        Account account = getAccount(id);
        account.withdraw(amount);
        return account;
    }

    public List<Account> accountsOf(String owner) {
        return accounts.values().stream()
                .filter(account -> account.getOwner().equals(owner))
                .toList();
    }

    public BigDecimal totalDeposits() {
        return accounts.values().stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void transfer(String fromId, String toId, BigDecimal amount) {
        if (fromId.equals(toId)) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }
        Account from = getAccount(fromId);
        Account to = getAccount(toId);

        from.withdraw(amount);
        to.deposit(amount);
    }
}