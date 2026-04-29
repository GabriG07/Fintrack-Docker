package com.fintrack.security;

import com.fintrack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor // Lombok: injeta UserRepository via construtor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;


    // Chamado pelo Spring Security para autenticar o usuário
    // Recebe o username (nesse caso, é o email)
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .map(u -> User.builder() // User do Spring Security, não o do Model
                        .username(u.getEmail())
                        .password(u.getPassword())
                        .roles("USER")
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Not found: " + email));
    }
}
