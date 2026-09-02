package com.cocciahouse.api.dto.menu;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record MenuItemRequest(

        @NotNull(message = "Recipe is required.")
        Long recipeId,

        @NotNull(message = "Display order is required.")
        @Min(
                value = 0,
                message = "Display order cannot be negative."
        )
        Integer displayOrder,

        @NotNull(message = "Visible status is required.")
        Boolean visible,

        @NotNull(message = "At least one price is required.")
        @Size(
                min = 1,
                message = "At least one price is required."
        )
        List<@Valid MenuItemPriceRequest> prices

) {
}