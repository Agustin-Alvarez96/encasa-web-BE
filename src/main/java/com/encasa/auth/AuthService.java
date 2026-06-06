package com.encasa.auth;

import com.encasa.auth.dto.LoginRequest;
import com.encasa.auth.dto.RegisterRequest;
import com.encasa.auth.dto.SyncRequest;
import com.encasa.models.User;
import com.encasa.repositories.UserRepository;
import com.encasa.security.JwtService;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
        user.setRole("USER");
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

    public String sync(SyncRequest request) {
        userRepository.findByEmail(request.email()).ifPresentOrElse(
                user -> {
                    if (user.getName() == null && request.name() != null) {
                        user.setName(request.name());
                        userRepository.save(user);
                    }
                },
                () -> {
                    User user = new User();
                    user.setEmail(request.email());
                    user.setPassword("GOOGLE_OAUTH_NO_PASSWORD");
                    user.setRole("USER");
                    user.setName(request.name());
                    userRepository.save(user);
                }
        );
        return jwtService.generateToken(request.email());
    }
}

