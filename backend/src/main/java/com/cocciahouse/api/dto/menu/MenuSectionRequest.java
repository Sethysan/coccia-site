package com.cocciahouse.api.dto.menu;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MenuSectionRequest(

        @NotBlank(message = "Section name is required.")
        @Size(
                max = 100,
                message = "Section name must be 100 characters or fewer."
        )
        String name,

        String subtitle,

        String footerText,

        @NotNull(message = "Display order is required.")
        @Min(
                value = 0,
                message = "Display order cannot be negative."
        )
        Integer displayOrder,

        @NotNull(message = "Active status is required.")
        Boolean active

) {
}