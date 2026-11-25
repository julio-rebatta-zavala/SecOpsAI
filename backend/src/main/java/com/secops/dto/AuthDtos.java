package com.secops.dto;

import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class AuthDtos {
    private AuthDtos() {
    }

    public record LoginRequest(@NotBlank @Size(max = 80) String username, @NotBlank @Size(max = 200) String password) {
    }

    public record TokenResponse(String accessToken, String refreshToken, long expiresInSeconds, String username,
            Set<String> roles, boolean mustChangePassword) {
    }

    public record RefreshRequest(@NotBlank String refreshToken) {
    }

    public record ChangePasswordRequest(@NotBlank String currentPassword,
            @NotBlank @Size(min = 14, max = 200) String newPassword) {
    }
}
