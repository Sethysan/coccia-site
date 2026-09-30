package com.cocciahouse.api.dto;

public record AuthSessionResponse(
        boolean authenticated,
        String username,
        String displayName,
        String role
) {
}