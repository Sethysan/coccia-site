
package com.cocciahouse.api.dto.purchasing;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record PurchasingOrderRequest(

        @NotNull
        Long departmentId,

        @NotNull
        Long vendorId,

        LocalDate expectedDeliveryDate,

        String notes,

        @NotEmpty
        List<@NotNull @Valid PurchasingOrderItemRequest> items

) {
}
