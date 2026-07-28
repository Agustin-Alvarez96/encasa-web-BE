package com.encasa.auth;

import com.encasa.auth.dto.UserProfileResponse;
import com.encasa.models.User;

public final class UserProfileMapper {

    private UserProfileMapper() {}

    public static UserProfileResponse toResponse(User user) {
        return new UserProfileResponse(
                String.valueOf(user.getId()),
                user.getEmail(),
                user.getName(),
                user.getPicture(),
                user.getRole(),
                user.isHasProfessionalProfile(),
                user.isEmailNotifications()
        );
    }
}
