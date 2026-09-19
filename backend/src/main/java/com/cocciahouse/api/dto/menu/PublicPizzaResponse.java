package com.cocciahouse.api.dto.menu;

import java.util.List;

public record PublicPizzaResponse(

        List<PublicPizzaSizeResponse> sizes,
        List<PublicPizzaToppingResponse> toppings,
        List<PublicPizzaAddOnResponse> addOns,
        List<PublicPizzaSpecialtyResponse> specialties

) {
}