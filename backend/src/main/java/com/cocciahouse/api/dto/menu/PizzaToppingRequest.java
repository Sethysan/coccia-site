package com.cocciahouse.api.dto.menu;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PizzaToppingRequest(

        @NotBlank
        @Size(max = 100)
        String name,

        @NotNull
        Boolean active

) {
}