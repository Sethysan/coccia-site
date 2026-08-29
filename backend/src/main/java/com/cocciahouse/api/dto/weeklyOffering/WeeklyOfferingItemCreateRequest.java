package com.cocciahouse.api.dto.weeklyOffering;

import com.cocciahouse.api.dto.recipe.OfferingItemPriceRequest;
import com.cocciahouse.api.model.OfferingType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record WeeklyOfferingItemCreateRequest(

        @NotNull
        Long recipeId,

        @NotNull
        OfferingType offeringType,

        boolean includesHouseSalad,

        boolean includesHomemadeBread,

        int displayOrder,

        @NotEmpty
        @Valid
        List<OfferingItemPriceRequest> prices

) {
}