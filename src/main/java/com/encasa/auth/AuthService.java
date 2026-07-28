package com.encasa.auth;

import com.encasa.auth.dto.AuthTokenResponse;
import com.encasa.auth.dto.LoginRequest;
import com.encasa.auth.dto.OAuthGoogleRequest;
import com.encasa.auth.dto.RegisterRequest;
import com.encasa.exceptions.BadRequestException;
import com.encasa.exceptions.ConflictException;
import com.encasa.models.User;
import com.encasa.repositories.UserRepository;
import com.encasa.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuthService {

    private static final String AUTH_PROVIDER_GOOGLE = "GOOGLE";
    private static final String AUTH_PROVIDER_LOCAL = "LOCAL";
    private static final String ROLE_PROFESSIONAL = "PROFESSIONAL";
    private static final String ROLE_USER = "USER";

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
        String email = normalizeEmail(request.email());
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ConflictException("Email already registered");
        }
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(ROLE_USER);
        user.setAuthProvider(AUTH_PROVIDER_LOCAL);
        userRepository.save(user);
    }

    public AuthTokenResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadRequestException("User not found"));
        return buildAuthResponse(user);
    }

    @Transactional
    public AuthTokenResponse oauthGoogle(OAuthGoogleRequest request) {
        String email = normalizeEmail(request.email());

        User user = resolveOAuthUser(request, email);
        applyProfessionalIntent(user, request.intent());
        syncProfileFromGoogle(user, request.name(), request.picture());

        userRepository.save(user);
        return buildAuthResponse(user);
    }

    private User resolveOAuthUser(OAuthGoogleRequest request, String email) {
        if (request.googleSub() != null && !request.googleSub().isBlank()) {
            var byGoogleSub = userRepository.findByGoogleSub(request.googleSub());
            if (byGoogleSub.isPresent()) {
                return byGoogleSub.get();
            }
        }

        var byEmail = userRepository.findByEmail(email);
        if (byEmail.isPresent()) {
            User existing = byEmail.get();
            if (existing.getGoogleSub() != null
                    && request.googleSub() != null
                    && !existing.getGoogleSub().equals(request.googleSub())) {
                throw new ConflictException("Email already linked to another Google account");
            }
            if (request.googleSub() != null && !request.googleSub().isBlank()) {
                existing.setGoogleSub(request.googleSub());
            }
            if (AUTH_PROVIDER_LOCAL.equals(existing.getAuthProvider())) {
                existing.setAuthProvider(AUTH_PROVIDER_GOOGLE);
            }
            return existing;
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        user.setRole(ROLE_USER);
        user.setAuthProvider(AUTH_PROVIDER_GOOGLE);
        if (request.googleSub() != null && !request.googleSub().isBlank()) {
            user.setGoogleSub(request.googleSub());
        }
        return user;
    }

    private void applyProfessionalIntent(User user, String intent) {
        if ("professional".equalsIgnoreCase(intent) && ROLE_USER.equals(user.getRole())) {
            user.setRole(ROLE_PROFESSIONAL);
        }
    }

    private void syncProfileFromGoogle(User user, String name, String picture) {
        if (name != null && !name.isBlank()) {
            user.setName(name.trim());
        }
        if (picture != null && !picture.isBlank()) {
            user.setPicture(picture.trim());
        }
    }

    private AuthTokenResponse buildAuthResponse(User user) {
        String token = jwtService.generateToken(user.getEmail());
        return new AuthTokenResponse(token, UserProfileMapper.toResponse(user));
    }

    private static String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new BadRequestException("Email is required");
        }
        return email.trim().toLowerCase();
    }
}
