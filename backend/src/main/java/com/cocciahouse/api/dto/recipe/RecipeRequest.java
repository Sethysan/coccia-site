package com.cocciahouse.api.dto.recipe;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RecipeRequest(

        @NotBlank(message = "Recipe name is required.")
        @Size(
                max = 150,
                message = "Recipe name must be 150 characters or fewer."
        )
        String name,

        String description,

        @Size(
                max = 255,
                message = "Image description must be 255 characters or fewer."
        )
        String imageAlt
) {
}