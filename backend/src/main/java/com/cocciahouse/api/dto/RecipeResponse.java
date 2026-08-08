package com.cocciahouse.api.dto;

public record RecipeResponse(
        Long id,
        String name,
        boolean active
) {
}