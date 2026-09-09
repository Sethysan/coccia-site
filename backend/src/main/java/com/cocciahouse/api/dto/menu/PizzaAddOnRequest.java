package com.cocciahouse.api.dto.menu;

import com.cocciahouse.api.model.PizzaAddOnType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record PizzaAddOnRequest(

        @NotBlank
        @Size(max = 100)
        String name,

        @NotNull
        @DecimalMin(value = "0.01")
        BigDecimal amount,

        @NotNull
        PizzaAddOnType type,

        @NotNull
        Boolean active

) {
}
