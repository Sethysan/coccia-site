package com.cocciahouse.api.dto.purchasing;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import java.math.BigDecimal;

public record PurchasingVendorRequest(

        @NotBlank(message = "Vendor name is required")
        @Size(max = 150)
        String name,

        @Size(max = 30)
        String phone,

        @Size(max = 500)
        String website,

        @Size(max = 150)
        String preferredDeliveryDays,

        LocalDate nextDeliveryDate,

        @Pattern(
                regexp = "WEEKLY|BIWEEKLY|AS_NEEDED",
                message = "Delivery frequency must be WEEKLY, BIWEEKLY, or AS_NEEDED"
        )
        String deliveryFrequency,

        String notes,

        @DecimalMin(value = "0.00", message = "Minimum order amount cannot be negative")
        BigDecimal minimumOrderAmount,

        @DecimalMin(value = "0.001", message = "Minimum order quantity must be greater than zero")
        BigDecimal minimumOrderQuantity,

        @Size(max = 50)
        String minimumOrderQuantityUnit,

        @DecimalMin(value = "0.00", message = "Below-minimum fee cannot be negative")
        BigDecimal belowMinimumFee,

        @DecimalMin(value = "0.00", message = "Off-schedule delivery fee cannot be negative")
        BigDecimal offScheduleDeliveryFee,

        boolean active

) {}