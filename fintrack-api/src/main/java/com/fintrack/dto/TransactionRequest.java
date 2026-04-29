package com.fintrack.dto;

import com.fintrack.model.Category;
import com.fintrack.model.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionRequest(
        @NotBlank String description,
        @NotNull @DecimalMin("0.01")BigDecimal amount,
        @NotNull TransactionType type,
        @NotNull Category category,
        @NotNull LocalDate date
        ) {
}
