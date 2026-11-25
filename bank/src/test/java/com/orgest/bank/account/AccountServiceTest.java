package com.orgest.bank.account;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AccountServiceTest {

    @Test
    void transferMovesMoneyBetweenAccounts() {
        AccountService accountService = new AccountService();
        Account alice = accountService.openAccount("Alice");
        Account bob = accountService.openAccount("Bob");
        alice.deposit(new BigDecimal("100"));

        accountService.transfer(alice.getId(), bob.getId(), new BigDecimal("40"));

        assertThat(alice.getBalance()).isEqualByComparingTo("60");
        assertThat(bob.getBalance()).isEqualByComparingTo("40");
    }

    @Test
    void transferToUnknownAccountThrows() {
        AccountService accountService = new AccountService();
        Account alice = accountService.openAccount("Alice");
        alice.deposit(new BigDecimal("100"));

        assertThrows(AccountNotFoundException.class,
                () -> accountService.transfer(alice.getId(), "does-not-exist", new BigDecimal("40")));

        assertThat(alice.getBalance()).isEqualByComparingTo("100");
    }

    @Test
    void findAccountReturnsEmptyForUnknownId() {
        AccountService accountService = new AccountService();

        assertThat(accountService.findAccount("does-not-exist")).isEmpty();
    }

    @Test
    void transferWithInsufficientFundsChangesNothing() {
        AccountService accountService = new AccountService();
        Account alice = accountService.openAccount("Alice");
        Account bob = accountService.openAccount("Bob");
        alice.deposit(new BigDecimal("30"));

        assertThrows(IllegalStateException.class,
                () -> accountService.transfer(alice.getId(), bob.getId(), new BigDecimal("50")));

        assertThat(alice.getBalance()).isEqualByComparingTo("30");
        assertThat(bob.getBalance()).isEqualByComparingTo("0");
    }

    @Test
    void accountsOfReturnsOnlyThatOwnersAccounts() {
        AccountService accountService = new AccountService();
        accountService.openAccount("Alice");
        accountService.openAccount("Alice");
        accountService.openAccount("Bob");

        List<Account> aliceAccounts = accountService.accountsOf("Alice");

        assertThat(aliceAccounts).hasSize(2);
        assertThat(aliceAccounts).allMatch(account -> account.getOwner().equals("Alice"));
    }
}