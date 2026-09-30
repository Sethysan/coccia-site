package com.cocciahouse.api.dto.user;

import java.time.Instant;

public record AdminUserResponse(
        Long id,
        String username,
        String displayName,
        String role,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {
}