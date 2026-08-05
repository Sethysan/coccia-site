package com.cocciahouse.api.dto;

import com.cocciahouse.api.model.OfferingType;

import java.util.List;

public record WeeklyOfferingItemResponse(
        Long id,
        Long recipeId,
        String recipeName,
        OfferingType offeringType,
        String publicTitle,
        String publicDescription,
        String imageUrl,
        String imageAlt,
        boolean includesHouseSalad,
        boolean includesHomemadeBread,
        String includedSidesText,
        int displayOrder,
        List<OfferingItemPriceResponse> prices
) {
}