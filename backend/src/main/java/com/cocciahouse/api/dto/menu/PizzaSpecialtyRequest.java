package com.cocciahouse.api.dto.menu;

import com.cocciahouse.api.model.PizzaSpecialtyPricingMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PizzaSpecialtyRequest(

        @NotNull
        Long recipeId,

        @NotNull
        Boolean active,

        @NotNull
        PizzaSpecialtyPricingMode pricingMode,

        List<Long> toppingIds,

        List<@Valid PizzaSpecialtyAddOnRequest> addOns,

        List<@Valid PizzaSpecialtyPriceRequest> prices

) {
}