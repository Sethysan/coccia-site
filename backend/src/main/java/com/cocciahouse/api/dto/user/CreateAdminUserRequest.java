package com.cocciahouse.api.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateAdminUserRequest(

        @NotBlank
        @Size(max = 100)
        String username,

        @NotBlank
        @Size(max = 100)
        String displayName,

        @NotBlank
        String role,

        @NotBlank
        @Size(min = 8, max = 100)
        String password

) {
}