package com.cocciahouse.api.dto.menu;


public record PizzaToppingResponse(

        Long id,

        Long menuSectionId,

        String name,

        int displayOrder,

        boolean active

) {
}
