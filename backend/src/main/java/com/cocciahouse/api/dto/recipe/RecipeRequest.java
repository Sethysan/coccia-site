package com.cocciahouse.api.dto.recipe;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RecipeRequest(

        @NotBlank(message = "Recipe name is required.")
        @Size(max = 150, message = "Recipe name must be 150 characters or fewer.")
        String name
) {
}