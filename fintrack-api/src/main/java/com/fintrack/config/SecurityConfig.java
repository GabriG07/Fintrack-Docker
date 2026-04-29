package com.fintrack.config;


import com.fintrack.security.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                //Desativa CSRF: não usamos cookies de sessão, logo CSRF não se aplica
                .csrf(AbstractHttpConfigurer::disable)

                //Configura CORS com a função corsSource()
                .cors(c -> c.configurationSource(corsSource()))

                // STATELESS: Spring não cria sessão HTTP, JWT é o único estado
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                //Regras de autorização
                .authorizeHttpRequests(auth -> auth.requestMatchers("/api/auth/**", "/swagger-ui/**",
                        "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated() //Todo_ o resto exige token válido
                )

                //JwtFilter executa antes do filtro padrão de autenticação
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    //BCrypt: algoritmo padrão para hash de senhas
    // Fator de custo padrão = 10 (lento o suficiente para dificultar brute force)
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    //AuthenticationManager: usado pela AuthService para validar email/senha
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception{
        return cfg.getAuthenticationManager();
    }

    //CORS: permite ao React(localhost:5173) acessar a API (localhost:8080)
    @Bean
    public CorsConfigurationSource corsSource(){
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(List.of("http://localhost:5173")); //origem do react
        cfg.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setAllowCredentials(true);
        return request -> cfg;
    }
}
