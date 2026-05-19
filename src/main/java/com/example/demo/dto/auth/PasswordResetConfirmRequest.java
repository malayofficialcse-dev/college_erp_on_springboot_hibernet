package com.example.demo.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record PasswordResetConfirmRequest(@NotBlank String token, @NotBlank String newPassword) {}
