
package com.cocciahouse.api.dto.purchasing;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PurchasingProductRequest(

        @NotNull
        Long departmentId,

        @NotNull
        Long categoryId,

        @NotBlank
        @Size(max = 200)
        String name,

        String description,

        boolean active

) {
}
