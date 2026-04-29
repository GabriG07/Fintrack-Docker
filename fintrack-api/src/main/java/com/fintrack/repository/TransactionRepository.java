package com.fintrack.repository;

import com.fintrack.model.Transaction;
import com.fintrack.model.TransactionType;
import com.fintrack.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserOrderByDateDesc(User user); // Busca todas as transações de um usuário, ordenadas pela data mais recente

    @Query("SELECT t FROM Transaction t WHERE t.user = :user AND YEAR(t.date) = :year AND " +
            "MONTH(t.date) = :month ORDER BY t.date DESC")
    List<Transaction> findByUserAndYearAndMonth(
          @Param("user") User user,
          @Param("year") int year,
          @Param("month") int month
    );

    // SUM agregado por tipo de transação em um mês específico
    // Retorna null se não houver transações, tratar com BigDecimal.ZERO no Service
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.user = :user AND t.type =:type AND YEAR(t.date) = :year AND MONTH(t.date) = :month")
    BigDecimal sumByUserTypeAndYearAndMonth(
            @Param("user") User user,
            @Param("type") TransactionType type,
            @Param("year") int year,
            @Param("month") int month
    );


}
