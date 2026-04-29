package com.fintrack.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    //Lê os valores do application.properties por meio de @Value

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    // Cria a chave HMAC-SHA256 a partir da string secreta
    private Key signingKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    //Gera o access token (JWT) para um usuário
    public String generateAccessToken(UserDetails userDetails){
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date()) //quando foi criado
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(signingKey(), SignatureAlgorithm.HS256)
                .compact(); //Serializa para string
    }

    //Extrai o email do token
    public String extractEmail(String token){
        return extractClaim(token, Claims::getSubject);
    }

    // Verifica se o token pertence ao usuário e não está expirado
    public boolean isTokenValid(String token, UserDetails userDetails){
        return extractEmail(token).equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token){
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    // Método_ genérico que extrai qualquer claim do payload do token
    private <T> T extractClaim(String token, Function<Claims, T> resolver){
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(signingKey()).build()
                .parseClaimsJws(token) //valida assinatura e parse
                .getBody();
        return resolver.apply(claims);
    }



}
