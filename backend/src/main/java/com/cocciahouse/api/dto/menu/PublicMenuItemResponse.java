
package com.cocciahouse.api.dto.menu;

import java.util.List;

public record PublicMenuItemResponse(

        String name,
        String description,
        String imageUrl,
        String imageAlt,

        List<PublicMenuItemPriceResponse> prices

) {
}