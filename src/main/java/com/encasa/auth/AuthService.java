package com.encasa.auth;

import com.encasa.auth.dto.LoginRequest;
import com.encasa.auth.dto.RegisterRequest;
import com.encasa.auth.dto.SyncRequest;
import com.encasa.models.Role;
import com.encasa.models.User;
import com.encasa.repositories.UserRepository;
import com.encasa.security.JwtService;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public void register(RegisterRequest request) {
        User user = new User();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.CLIENT);
        userRepository.save(user);
    }

    public String login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        return jwtService.generateToken(request.email());
    }

    /**
     * Usado por el frontend (NextAuth) en cada login, tanto para OAuth (Google) como
     * para credenciales: crea el usuario si todavía no existe y devuelve un JWT propio
     * del backend, sin pedir contraseña (la identidad ya fue validada por NextAuth).
     */
    public String sync(SyncRequest request) {
        User user = userRepository.findByEmail(request.email()).orElseGet(() -> {
            User created = new User();
            created.setEmail(request.email());
            created.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
            created.setRole(Role.CLIENT);
            return userRepository.save(created);
        });

        return jwtService.generateToken(user.getEmail());
    }
}

