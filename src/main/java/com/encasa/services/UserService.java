package com.encasa.services;

import com.encasa.models.User;
import com.encasa.repositories.UserRepository;
import com.encasa.users.dto.ChangePasswordRequest;
import com.encasa.users.dto.UpdateProfileRequest;
import com.encasa.users.dto.UserProfileResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserProfileResponse getProfile(String email) {
        return toResponse(findByEmail(email));
    }

    public UserProfileResponse updateProfile(String email, UpdateProfileRequest req) {
        User user = findByEmail(email);
        if (req.name() != null)     user.setName(req.name());
        if (req.phone() != null)    user.setPhone(req.phone());
        if (req.avatar() != null)   user.setAvatar(req.avatar());
        if (req.bio() != null)      user.setBio(req.bio());
        if (req.location() != null) user.setLocation(req.location());
        return toResponse(userRepository.save(user));
    }

    public void changePassword(String email, ChangePasswordRequest req) {
        User user = findByEmail(email);
        if (!passwordEncoder.matches(req.currentPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(req.newPassword()));
        userRepository.save(user);
    }

    private User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private UserProfileResponse toResponse(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getPhone(),
                user.getAvatar(),
                user.getBio(),
                user.getLocation(),
                user.getRole()
        );
    }
}
