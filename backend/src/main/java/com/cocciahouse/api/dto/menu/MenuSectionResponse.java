package com.cocciahouse.api.dto.menu;

public record MenuSectionResponse(

        Long id,
        String name,
        String subtitle,
        String footerText,
        int displayOrder,
        boolean active

) {
}