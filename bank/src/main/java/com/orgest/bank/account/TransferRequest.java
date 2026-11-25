package com.orgest.bank.account;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransferRequest(
        @NotBlank String fromId,
        @NotBlank String toId,
        @NotNull @Positive @Digits(integer = 12, fraction = 2) BigDecimal amount
) {
}