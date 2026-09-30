package com.cocciahouse.api.dto.menu;

import java.math.BigDecimal;

public record MenuSubsectionResponse(
        Long id,
        String name,
        BigDecimal price,
        int displayOrder,
        boolean active
) {}