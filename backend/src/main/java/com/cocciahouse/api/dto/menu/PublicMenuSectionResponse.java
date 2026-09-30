package com.cocciahouse.api.dto.menu;

import java.util.List;

public record PublicMenuSectionResponse(

        String name,
        String subtitle,
        String footerText,

        List<PublicMenuSubsectionResponse> subsections,
        List<PublicMenuItemResponse> items,

        PublicPizzaResponse pizza

) {
}