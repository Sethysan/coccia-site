package com.cocciahouse.api.controller.admin;

import com.cocciahouse.api.dto.menu.PizzaSizeRequest;
import com.cocciahouse.api.dto.menu.PizzaSizeResponse;
import com.cocciahouse.api.service.PizzaSizeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(
        "/api/admin/menu/sections/{menuSectionId}/pizza/sizes"
)
public class PizzaSizeAdminController {

    private final PizzaSizeService pizzaSizeService;

    public PizzaSizeAdminController(
            PizzaSizeService pizzaSizeService
    ) {
        this.pizzaSizeService = pizzaSizeService;
    }

    @GetMapping
    public List<PizzaSizeResponse> getSizes(
            @PathVariable Long menuSectionId
    ) {
        return pizzaSizeService.getSizes(menuSectionId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PizzaSizeResponse createSize(
            @PathVariable Long menuSectionId,
            @Valid @RequestBody PizzaSizeRequest request
    ) {
        return pizzaSizeService.createSize(
                menuSectionId,
                request
        );
    }

    @PutMapping("/{pizzaSizeId}")
    public PizzaSizeResponse updateSize(
            @PathVariable Long menuSectionId,
            @PathVariable Long pizzaSizeId,
            @Valid @RequestBody PizzaSizeRequest request
    ) {
        return pizzaSizeService.updateSize(
                menuSectionId,
                pizzaSizeId,
                request
        );
    }
}