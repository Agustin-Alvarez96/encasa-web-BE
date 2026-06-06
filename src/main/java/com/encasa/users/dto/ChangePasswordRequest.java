package com.encasa.users.dto;

public record ChangePasswordRequest(
        String currentPassword,
        String newPassword
) {}
