package com.cocciahouse.api.dto.menu;

import java.util.List;

public record MenuItemResponse(

        Long id,

        Long menuSectionId,

        Long recipeId,
        String recipeName,
        String description,
        String imageUrl,
        String imageAlt,

        int displayOrder,
        boolean visible,

        List<MenuItemPriceResponse> prices

) {
}