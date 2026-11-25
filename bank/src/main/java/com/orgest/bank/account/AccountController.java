package com.orgest.bank.account;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse open(@Valid @RequestBody OpenAccountRequest request) {
        Account account = accountService.openAccount(request.owner());
        return AccountResponse.from(account);
    }

    @GetMapping("/{id}")
    public AccountResponse get(@PathVariable String id) {
        return AccountResponse.from(accountService.getAccount(id));
    }

    @GetMapping
    public List<AccountResponse> listByOwner(@RequestParam String owner) {
        return accountService.accountsOf(owner).stream()
                .map(AccountResponse::from)
                .toList();
    }

    @PostMapping("/{id}/deposit")
    public AccountResponse deposit(@PathVariable String id, @Valid @RequestBody AmountRequest request) {
        return AccountResponse.from(accountService.deposit(id, request.amount()));
    }

    @PostMapping("/{id}/withdraw")
    public AccountResponse withdraw(@PathVariable String id, @Valid @RequestBody AmountRequest request) {
        return AccountResponse.from(accountService.withdraw(id, request.amount()));
    }

    @GetMapping("/{id}/transactions")
    public List<Transaction> history(@PathVariable String id) {
        Account account = accountService.getAccount(id);
        return account.getTransactions();
    }
}