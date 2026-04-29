package com.fintrack.controller;

import com.fintrack.dto.AuthResponse;
import com.fintrack.dto.LoginRequest;
import com.fintrack.dto.RefreshRequest;
import com.fintrack.dto.RegisterRequest;
import com.fintrack.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req){
        // @Valid ativa as validações do DTO (@NotBlank, @Email, @Size...)
        // @RequestBody deserializa o JSON do corpo da requisição
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(req));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req){
        return ResponseEntity.ok(authService.login(req));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshRequest req) {
        return ResponseEntity.ok(authService.refresh(req.refreshToken()));
    }
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody RefreshRequest req) {
        authService.logout(req.refreshToken());
        return ResponseEntity.noContent().build(); // 204 No Content
    }




}
