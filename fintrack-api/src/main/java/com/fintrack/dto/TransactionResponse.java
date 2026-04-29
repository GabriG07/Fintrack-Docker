package com.fintrack.dto;

import com.fintrack.model.Category;
import com.fintrack.model.Transaction;
import com.fintrack.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

//È retornado ao listar transações
public record TransactionResponse(
        Long id,
        String description,
        BigDecimal amount,
        TransactionType type,
        Category category,
        LocalDate date
) {

    //Método_ estático de conversão: Transaction (entidade) -> TransactionResponse (DTO)
    // Evita expor campos internos como user_id, createdAt etc.
    public static TransactionResponse from(Transaction t) {
        return new TransactionResponse(
                t.getId(), t.getDescription(), t.getAmount(), t.getType(), t.getCategory(), t.getDate()
        );
    }
}
