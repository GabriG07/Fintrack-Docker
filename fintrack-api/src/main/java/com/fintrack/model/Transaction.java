package com.fintrack.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relacionamento N:1 com User - muitas transações para 1 usuário
    // LAZY: só carrega quando o User do banco for acessado explicitamente
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // coluna chave estrangeira na tabela
    private User user;

    @Column(nullable = false)
    private String description;

    // BigDecimal para dinheiro: precisão exata, sem arredondamento de float/double
    // DECIMAL(15,2) no banco = até 15 dígitos, com 2 casas decimais
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING) // Salva "INCOME"/"EXPENSE" como texto no banco
    @Column(nullable = false)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }


}
