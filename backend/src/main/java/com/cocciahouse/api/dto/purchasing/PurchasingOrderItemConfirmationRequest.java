
package com.cocciahouse.api.dto.purchasing;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record PurchasingOrderItemConfirmationRequest(

        @NotNull
        Long orderItemId,

        @NotNull
        @DecimalMin("0.0")
        @Digits(integer = 9, fraction = 3)
        BigDecimal confirmedQuantity,

        @NotNull
        @DecimalMin("0.0")
        @Digits(integer = 10, fraction = 2)
        BigDecimal confirmedUnitPrice

) {
}
