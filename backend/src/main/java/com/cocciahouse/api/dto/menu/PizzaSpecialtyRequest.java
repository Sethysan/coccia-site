package com.cocciahouse.api.dto.menu;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PizzaSpecialtyRequest(

        @NotNull
        Long recipeId,

        @NotNull
        Boolean active,

        @NotNull
        @Size(min = 1)
        List<@Valid PizzaSpecialtyPriceRequest> prices

) {
}