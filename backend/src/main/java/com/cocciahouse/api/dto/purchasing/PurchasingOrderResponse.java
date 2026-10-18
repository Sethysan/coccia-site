package com.cocciahouse.api.dto.purchasing;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

public record PurchasingOrderResponse(

        Long id,
        Long departmentId,
        Long vendorId,
        String vendorName,
        String status,
        LocalDate expectedDeliveryDate,
        OffsetDateTime confirmedAt,
        String notes,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        List<PurchasingOrderItemResponse> items
) {
}