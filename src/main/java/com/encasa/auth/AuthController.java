package com.encasa.auth;

import com.encasa.auth.dto.AuthTokenResponse;
import com.encasa.auth.dto.LoginRequest;
import com.encasa.auth.dto.OAuthGoogleRequest;
import com.encasa.auth.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "User registered"));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthTokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/oauth/google")
    public ResponseEntity<AuthTokenResponse> oauthGoogle(@Valid @RequestBody OAuthGoogleRequest request) {
        return ResponseEntity.ok(authService.oauthGoogle(request));
    }
}
