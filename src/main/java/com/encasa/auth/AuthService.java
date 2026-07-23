package com.encasa.auth;

import com.encasa.auth.dto.AuthResponse;
import com.encasa.auth.dto.LoginRequest;
import com.encasa.auth.dto.RegisterRequest;
import com.encasa.auth.dto.SyncRequest;
import com.encasa.models.User;
import com.encasa.repositories.UserRepository;
import com.encasa.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El email ya está registrado");
        }
        User user = new User();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setName(request.name());
        user.setRole("client");
        userRepository.save(user);

        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(token, user.getId(), user.getEmail(), user.getName(), user.getRole());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(token, user.getId(), user.getEmail(), user.getName(), user.getRole());
    }

    public AuthResponse sync(SyncRequest request) {
        User user = userRepository.findByEmail(request.email()).orElse(null);
        if (user == null) {
            user = new User();
            user.setEmail(request.email());
            user.setPassword("GOOGLE_OAUTH_NO_PASSWORD");
            user.setRole("client");
            user.setName(request.name());
            user.setAvatar(request.image());
        } else {
            if (user.getName() == null && request.name() != null) user.setName(request.name());
            if (request.image() != null) user.setAvatar(request.image());
        }
        userRepository.save(user);
        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(token, user.getId(), user.getEmail(), user.getName(), user.getRole());
    }
}
