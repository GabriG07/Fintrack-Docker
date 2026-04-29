package com.fintrack.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

//@Email e @NotBlank sao ativados pelo @Valid no Controller
public record LoginRequest(
        @Email @NotBlank String email,
        @NotBlank String password
) {}
