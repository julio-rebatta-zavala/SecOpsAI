package com.secops.dto;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import com.secops.entity.Role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public final class AdminDtos {
	
    private AdminDtos() {
    }

    public record CreateUserRequest(@NotBlank String username, @NotBlank @Size(min = 14) String temporaryPassword,
            @NotEmpty Set<Role> roles) {
    }

    public record UpdateUserRequest(Boolean enabled, Set<Role> roles) {
    }

    public record UserView(UUID id, String username, boolean enabled, boolean mustChangePassword, Set<Role> roles,
            Instant createdAt) {
    }

    public record PromptRequest(@NotBlank @Size(max = 20000) String content) {
    }

    public record PromptView(UUID id, int version, String content, String hash, Instant createdAt) {
    }

    public record DocumentView(UUID id, String name, String hash, String status, String source, int chunks,
            String rejectionReason, Instant createdAt) {
    }
    
}
