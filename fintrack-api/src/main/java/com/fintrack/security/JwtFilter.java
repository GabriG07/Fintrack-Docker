package com.fintrack.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
// OncePerRequestFilter: garante que o filtro execute exatamente 1 vez por requisição
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. Busca o header "Authorization"
        final String header = request.getHeader("Authorization");

        // 2. Se não tem token ou não começa com "Bearer ", passa adiante sem autenticar
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            //3. Remove "Bearer " e extrai o email do token
            final String token = header.substring(7);
            final String email = jwtUtil.extractEmail(token);

            // 4. Só autentica se ainda não há usuário no contexto de segurança
            if(email != null && SecurityContextHolder.getContext().getAuthentication() == null){
                UserDetails ud = userDetailsService.loadUserByUsername(email);

                if(jwtUtil.isTokenValid(token, ud)){
                    // 5. Cria o objeto de autenticação e registra no SecurityContext
                    var auth = new UsernamePasswordAuthenticationToken(ud, null, ud.getAuthorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    // A partir daqui, @AuthenticationPrincipal funciona nos Controllers
                }
            }
        } catch (Exception e) {
            // Token inválido: não autentica, deixa o Spring retornar 401
        }

        filterChain.doFilter(request, response); //Continua para o próximo filtro/Controller

    }
}
