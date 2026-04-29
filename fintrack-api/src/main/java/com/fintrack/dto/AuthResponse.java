package com.fintrack.dto;

//É a resposta retornada pelo servidor após login ou registro
public record AuthResponse(
        String accessToken, //JWT de curta duração
        String refreshToken, //UUID de longa duração, salvo no banco
        String name,
        String email
) {
}
