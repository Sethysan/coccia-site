package com.cocciahouse.api.dto.menu;

import java.util.List;

public record PublicMenuResponse(

        List<PublicMenuSectionResponse> sections

) {
}