package com.cocciahouse.api.dto.weeklyOffering;

import com.cocciahouse.api.dto.recipe.OfferingItemPriceResponse;
import com.cocciahouse.api.model.OfferingType;

import java.util.List;

public record WeeklyOfferingItemResponse(
        Long id,
        Long recipeId,
        String recipeName,
        OfferingType offeringType,
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