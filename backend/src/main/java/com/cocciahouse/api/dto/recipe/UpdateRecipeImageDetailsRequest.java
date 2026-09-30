package com.cocciahouse.api.dto.recipe;

import jakarta.validation.constraints.Size;

public record UpdateRecipeImageDetailsRequest(

        @Size(
                max = 255,
                message = "Image description must be 255 characters or fewer."
        )
        String imageAlt,

        @Size(
                max = 255,
                message = "Image caption must be 255 characters or fewer."
        )
        String imageCaption

) {
}