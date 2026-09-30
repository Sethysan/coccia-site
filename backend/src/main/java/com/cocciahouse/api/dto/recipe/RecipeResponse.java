package com.cocciahouse.api.dto.recipe;

public record RecipeResponse(
        Long id,
        String name,
        String description,
        String imageUrl,
        String imageAlt,
        String imageCaption,
        boolean active
) {
}