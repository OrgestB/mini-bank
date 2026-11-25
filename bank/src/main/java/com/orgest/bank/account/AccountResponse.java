package com.orgest.bank.account;

import java.math.BigDecimal;

public record AccountResponse(String id, String owner, BigDecimal balance) {

    public static AccountResponse from(Account account) {
        return new AccountResponse(account.getId(), account.getOwner(), account.getBalance());
    }
}