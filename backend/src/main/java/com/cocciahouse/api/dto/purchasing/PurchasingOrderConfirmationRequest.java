
package com.cocciahouse.api.dto.purchasing;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record PurchasingOrderConfirmationRequest(

        @NotEmpty
        List<@NotNull @Valid PurchasingOrderItemConfirmationRequest> items

) {
}
