package com.fintrack.service;

import com.fintrack.dto.AuthResponse;
import com.fintrack.dto.LoginRequest;
import com.fintrack.dto.RegisterRequest;
import com.fintrack.exception.ApiException;
import com.fintrack.model.RefreshToken;
import com.fintrack.model.User;
import com.fintrack.repository.RefreshTokenRepository;
import com.fintrack.repository.UserRepository;
import com.fintrack.security.JwtUtil;
import com.fintrack.security.UserDetailsServiceImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationManager authenticationManager;

    // @Transactional: se qualquer operação falhar, tudo é desfeito (rollback)
    @Transactional
    public AuthResponse register(RegisterRequest req){
        if(userRepository.existsByEmail(req.email())){
            //
        }

        var user = User.builder()
                .name(req.name())
                .email(req.email())
                .password(passwordEncoder.encode(req.password()))
                .build();

        userRepository.save(user);

        //Gera os tokens
        var ud = userDetailsService.loadUserByUsername(user.getEmail());
        String accessToken = jwtUtil.generateAccessToken(ud);
        String refreshToken = createRefreshToken(user);

        return new AuthResponse(accessToken, refreshToken, user.getName(), user.getEmail());
    }

    @Transactional
    public AuthResponse login(LoginRequest req){
        // authenticate() lança BadCredentialsException se email/senha inválidos
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(req.email(), req.password()));
        var user = userRepository. findByEmail(req.email())
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));

        var ud = userDetailsService.loadUserByUsername(user.getEmail());
        String accessToken = jwtUtil.generateAccessToken(ud);
        String refreshToken = createRefreshToken(user);

        return new AuthResponse(accessToken, refreshToken, user.getName(), user.getEmail());
    }

    @Transactional
    public AuthResponse refresh(String tokenStr){
        //Busca o refresh token no banco
        var rt = refreshTokenRepository.findByToken(tokenStr)
                .orElseThrow(() -> new ApiException("Invalid refresh token", HttpStatus. UNAUTHORIZED));

        //Verifica se expirou
        if(rt.isExpired()){
            refreshTokenRepository.delete(rt); //Limpa o token expirado
            throw new ApiException("Refresh token expired, please login again", HttpStatus.UNAUTHORIZED);
        }

        //Gera um novo access token, mantendo o refresh token
        var ud = userDetailsService.loadUserByUsername(rt.getUser().getEmail());
        return new AuthResponse(jwtUtil.generateAccessToken(ud), tokenStr, rt.getUser().getName(), rt.getUser().getPassword());
    }

    @Transactional
    public void logout(String tokenStr){
        //remove o refresh token do banco
        refreshTokenRepository.findByToken(tokenStr).ifPresent(refreshTokenRepository::delete);
    }

    // Cria um novo refresh token, deletando o anterior se existir
    private String createRefreshToken(User user){
        refreshTokenRepository.deleteByUser(user);  //manter apenas 1 token por usuário
        refreshTokenRepository.flush(); // força o DELETE antes do INSERT
        var rt = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusSeconds(7 * 24 * 60 * 60)) //7dias
                .build();
        return refreshTokenRepository.save(rt).getToken();
    }





}
