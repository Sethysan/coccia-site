package com.cocciahouse.api.dto.menu;

import com.cocciahouse.api.model.PizzaSpecialtyPricingMode;

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

        PizzaSpecialtyPricingMode pricingMode,

        List<Long> toppingIds,

        List<PizzaSpecialtyAddOnResponse> addOns,

        List<PizzaSpecialtyPriceResponse> prices

) {
}