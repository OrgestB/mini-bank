package com.orgest.bank.account;

import java.math.BigDecimal;
import java.time.Instant;

public record Transaction(
        TransactionType type,
        BigDecimal amount,
        BigDecimal balanceAfter,
        Instant timestamp
) {
}
