package com.cocciahouse.api.dto;

import com.cocciahouse.api.model.OfferingType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record WeeklyOfferingItemCreateRequest(

        @NotNull
        Long recipeId,

        @NotNull
        OfferingType offeringType,

        String publicDescription,

        String imageUrl,

        String imageAlt,

        boolean includesHouseSalad,

        boolean includesHomemadeBread,

        int displayOrder,

        @NotEmpty
        @Valid
        List<OfferingItemPriceRequest> prices

) {
}