package com.encasa.auth;

import com.encasa.auth.dto.AuthResponse;
import com.encasa.auth.dto.LoginRequest;
import com.encasa.auth.dto.RegisterRequest;
import com.encasa.auth.dto.SyncRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/sync")
    public ResponseEntity<AuthResponse> sync(@Valid @RequestBody SyncRequest request) {
        return ResponseEntity.ok(authService.sync(request));
    }
}
