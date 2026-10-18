package com.cocciahouse.api.dto.purchasing;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;
import java.util.List;

public record PurchasingReceiptRequest(

        String notes,

        @NotEmpty
        List<@NotNull @Valid ReceiptItemRequest> items

) {

    public record ReceiptItemRequest(

            @NotNull
            Long orderItemId,

            @NotNull
            @DecimalMin(value = "0.001")
            BigDecimal receivedQuantity

    ) {}
}
