package com.cocciahouse.api.dto.menu;

import java.util.List;

public record PizzaSpecialtyResponse(

        Long id,

        Long menuSectionId,

        Long recipeId,

        String recipeName,

        String description,

        String imageUrl,

        String imageAlt,

        int displayOrder,

        boolean active,

        List<PizzaSpecialtyPriceResponse> prices

) {
}