package com.fintrack.dto;

import java.math.BigDecimal;

public record MonthSummaryResponse(
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal balance,
        int month,
        int year
) {
}
