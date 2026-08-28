package com.cocciahouse.api.dto.recipe;

public record RecipeResponse(
        Long id,
        String name,
        boolean active
) {
}