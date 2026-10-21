package com.cocciahouse.api.dto.purchasing;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PurchasingReceiptResponse(

        Long id,

        Long orderId,

        LocalDateTime receivedAt,

        String notes,

        List<ReceiptItemResponse> items

) {

    public record ReceiptItemResponse(

            Long id,

            Long orderItemId,

            BigDecimal receivedQuantity

    ) {}
}
