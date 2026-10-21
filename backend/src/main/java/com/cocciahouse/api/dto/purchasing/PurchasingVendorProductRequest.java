
package com.cocciahouse.api.dto.purchasing;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record PurchasingVendorProductRequest(

        @NotNull
        Long productId,

        @NotNull
        Long vendorId,

        @Size(max = 100)
        String vendorSku,

        @NotBlank
        @Size(max = 50)
        String orderUnit,

        @NotNull
        @DecimalMin("0.001")
        @Digits(integer = 9, fraction = 3)
        BigDecimal packageQuantity,

        @NotBlank
        @Size(max = 50)
        String packageUnit,

        @DecimalMin("0.00")
        @Digits(integer = 10, fraction = 2)
        BigDecimal unitPrice,

        boolean preferred,

        boolean active

) {
}
