package com.fintrack.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter @Setter // Lombok: gera getters e setters
@NoArgsConstructor // Lombok: construtor sem argumentos (exigido pelo JPA)
@AllArgsConstructor // Lombok: construtor com todos os campos
@Builder // Lombok: permite User.builder().name("x").build()
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist //marca um método_ que será executado automaticamente antes da entidade ser inserida no banco
    protected void prePersist(){
        this.createdAt = LocalDateTime.now();
    }
}
