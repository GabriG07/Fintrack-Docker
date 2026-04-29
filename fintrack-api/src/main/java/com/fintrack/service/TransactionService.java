package com.fintrack.service;

import com.fintrack.dto.MonthSummaryResponse;
import com.fintrack.dto.TransactionRequest;
import com.fintrack.dto.TransactionResponse;
import com.fintrack.exception.ApiException;
import com.fintrack.model.Transaction;
import com.fintrack.model.TransactionType;
import com.fintrack.model.User;
import com.fintrack.repository.TransactionRepository;
import com.fintrack.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public List<TransactionResponse> getTransactions(String email, Integer month, Integer year) {
        User user = getUser(email);

        //Se mês e ano foram informados, então filtra. Do contrário, retorna todas
        List<Transaction> list = (month != null && year != null)
                ? transactionRepository.findByUserAndYearAndMonth(user, year, month)
                : transactionRepository.findByUserOrderByDateDesc(user);
        return list.stream().map(TransactionResponse::from).toList();
    }

    @Transactional
    public TransactionResponse create(String email, TransactionRequest req) {
        User user = getUser(email);
        var t = Transaction.builder()
                .user(user)
                .description(req.description())
                .amount(req.amount())
                .type(req.type())
                .category(req.category())
                .date(req.date())
                .build();

        return TransactionResponse.from(transactionRepository.save(t));
    }

    @Transactional
    public void delete(String email, Long id) {
        var t = transactionRepository.findById(id)
                .orElseThrow(() -> new ApiException("Transaction not found", HttpStatus.NOT_FOUND));

        //Verifica se a transação pertence ao usuário que está deletando
        if (!t.getUser().getEmail().equals(email)) {
            throw new ApiException("Access denied", HttpStatus.FORBIDDEN);
        }

        transactionRepository.delete(t);
    }

    public MonthSummaryResponse getSummary(String email, int month, int year) {
        User user = getUser(email);
        BigDecimal income = nullSafe(transactionRepository.sumByUserTypeAndYearAndMonth(user, TransactionType.INCOME, year, month));
        BigDecimal expense = nullSafe(transactionRepository.sumByUserTypeAndYearAndMonth(user, TransactionType.EXPENSE, year, month));
        return new MonthSummaryResponse(income, expense, income.subtract(expense), month, year);
    }

    //Retorna o resumo dos últimos 6 meses para o gráfico de barras
    public List<MonthSummaryResponse> getLast6MonthsSummary(String email) {
        User user = getUser(email);
        List<MonthSummaryResponse> result = new ArrayList<>();
        LocalDate now = LocalDate.now();

        for (int i = 5; i >= 0; i--) {
            LocalDate d = now.minusMonths(i);
            int m = d.getMonthValue();
            int y = d.getYear();
            BigDecimal income = nullSafe(transactionRepository
                    .sumByUserTypeAndYearAndMonth(user, TransactionType.INCOME, y, m));
            BigDecimal expense = nullSafe(transactionRepository
                    .sumByUserTypeAndYearAndMonth(user, TransactionType.EXPENSE, y, m));

            result.add(new MonthSummaryResponse(income, expense, income.subtract(expense), m, y));
        }
        return result;
    }

    //Trata o null que SUM()retorna quando não há registros
    private BigDecimal nullSafe(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));
    }
}
