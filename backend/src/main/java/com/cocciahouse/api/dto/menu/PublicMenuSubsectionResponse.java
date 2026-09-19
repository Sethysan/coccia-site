package com.cocciahouse.api.dto.menu;

import java.math.BigDecimal;
import java.util.List;

public record PublicMenuSubsectionResponse(

        String name,
        BigDecimal price,

        List<PublicMenuItemResponse> items

) {
}