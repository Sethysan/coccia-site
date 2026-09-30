package com.cocciahouse.api.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateAdminUserRequest(

        @NotBlank
        @Size(max = 100)
        String displayName,

        @NotBlank
        String role,

        boolean active

) {
}