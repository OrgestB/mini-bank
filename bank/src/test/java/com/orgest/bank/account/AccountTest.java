package com.orgest.bank.account;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AccountTest {

    @Test
    void newAccountStartsWithZeroBalance() {
        Account account = new Account("ACC-1", "Orgest");

        assertEquals(BigDecimal.ZERO, account.getBalance());
    }

    @Test
    void depositIncreasesBalance() {
        Account account = new Account("ACC-1", "Orgest");

        account.deposit(new BigDecimal("100.50"));

        assertEquals(new BigDecimal("100.50"), account.getBalance());
    }

    @Test
    void depositRejectsNegativeAmount() {
        Account account = new Account("ACC-1", "Orgest");

        assertThrows(IllegalArgumentException.class,
                () -> account.deposit(new BigDecimal("-5")));
    }

    @Test
    void withdrawDecreasesBalance() {
        Account account = new Account("ACC-1", "Orgest");
        account.deposit(new BigDecimal("50"));

        account.withdraw(new BigDecimal("25"));

        assertEquals(new BigDecimal("25"), account.getBalance());

    }

    @Test
    void withdrawMoreThanBalanceThrows() {
        Account account = new Account("ACC-1", "Orgest");
        account.deposit(new BigDecimal("50"));

        account.withdraw(new BigDecimal("50"));

        assertThrows(InsufficientFundsException.class,
                () -> account.withdraw(new BigDecimal("51")));
    }

    @Test
    void withdrawEntireBalanceLeavesZero() {
        Account account = new Account("ACC-1", "Orgest");
        account.deposit(new BigDecimal("50"));

        account.withdraw(new BigDecimal("50"));

        assertEquals(BigDecimal.ZERO, account.getBalance());
    }

    @Test
    void depositIsRecordedInHistory() {
        Account account = new Account("ACC-1", "Orgest");

        account.deposit(new BigDecimal("100"));

        List<Transaction> history = account.getTransactions();
        assertThat(history).hasSize(1);
        assertThat(history.get(0).type()).isEqualTo(TransactionType.DEPOSIT);
        assertThat(history.get(0).amount()).isEqualByComparingTo("100");
    }

    @Test
    void historyCannotBeModifiedFromOutside() {
        Account account = new Account("ACC-1", "Orgest");
        account.deposit(new BigDecimal("100"));

        assertThrows(UnsupportedOperationException.class,
                () -> account.getTransactions().clear());
    }

    @Test
    void withdrawalIsRecordedWithBalanceAfter() {
        Account account = new Account("ACC-1", "Orgest");
        account.deposit(new BigDecimal("100"));

        account.withdraw(new BigDecimal("30"));

        List<Transaction> history = account.getTransactions();
        assertThat(history).hasSize(2);
        assertThat(history.get(1).type()).isEqualTo(TransactionType.WITHDRAWAL);
        assertThat(history.get(1).amount()).isEqualByComparingTo("30");
        assertThat(history.get(1).balanceAfter()).isEqualByComparingTo("70");
    }

    @Test
    void failedWithdrawalIsNotRecorded() {
        Account account = new Account("ACC-1", "Orgest");
        account.deposit(new BigDecimal("50"));

        assertThrows(InsufficientFundsException.class,
                () -> account.withdraw(new BigDecimal("80")));

        assertThat(account.getTransactions()).hasSize(1);
    }
}