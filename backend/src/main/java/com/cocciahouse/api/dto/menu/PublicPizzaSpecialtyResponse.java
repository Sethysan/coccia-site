package com.cocciahouse.api.dto.menu;

import com.cocciahouse.api.model.PizzaSpecialtyPricingMode;

import java.util.List;

public record PublicPizzaSpecialtyResponse(

        String name,
        String description,
        String imageUrl,
        String imageAlt,
        PizzaSpecialtyPricingMode pricingMode,
        List<String> toppings,
        List<PublicPizzaSpecialtyAddOnResponse> addOns,
        List<PublicPizzaItemPriceResponse> prices

) {
}