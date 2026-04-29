package com.fintrack.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Um usuário só pode ter 1 refresh token ativo em dado momento
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, unique = true)
    private String token; // gerado automaticamente

    @Column(nullable = false)
    private Instant expiryDate; //Instant é um ponto no tempo que não considera fuso-horário

    public boolean isExpired(){
        return expiryDate.isAfter(Instant.now());
    }

}
