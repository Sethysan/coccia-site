
package com.cocciahouse.api.dto.purchasing;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record PurchasingVendorResponse(

        Long id,

        String name,

        String phone,

        String website,

        String preferredDeliveryDays,

        LocalDate nextDeliveryDate,

        String deliveryFrequency,

        String notes,

        BigDecimal minimumOrderAmount,

        BigDecimal minimumOrderQuantity,

        String minimumOrderQuantityUnit,

        BigDecimal belowMinimumFee,

        BigDecimal offScheduleDeliveryFee,

        boolean active,

        Instant createdAt,

        Instant updatedAt

) {}
