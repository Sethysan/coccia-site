package com.cocciahouse.api.dto.menu;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record MenuItemRequest(

        @NotNull(message = "Recipe is required.")
        Long recipeId,

        Long menuSubsectionId,

        @NotNull(message = "Display order is required.")
        @Min(
                value = 0,
                message = "Display order cannot be negative."
        )
        Integer displayOrder,

        @NotNull(message = "Visible status is required.")
        Boolean visible,

        @NotNull(message = "Prices are required.")
        List<@Valid MenuItemPriceRequest> prices
) {

        public MenuItemRequest(
                Long recipeId,
                Integer displayOrder,
                Boolean visible,
                List<MenuItemPriceRequest> prices
        ) {
                this(
                        recipeId,
                        null,
                        displayOrder,
                        visible,
                        prices
                );
        }
}