package com.cocciahouse.api.dto.menu;

import java.util.List;

public record MenuItemResponse(

        Long id,

        Long menuSectionId,

        Long menuSubsectionId,
        String menuSubsectionName,

        Long recipeId,
        String recipeName,
        String description,
        String imageUrl,
        String imageAlt,
        String imageCaption,

        int displayOrder,
        boolean visible,

        List<MenuItemPriceResponse> prices

) {
}