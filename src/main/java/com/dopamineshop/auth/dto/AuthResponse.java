package com.dopamineshop.auth.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String displayName,
        String email
) {}
