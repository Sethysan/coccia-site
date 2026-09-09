package com.cocciahouse.api.controller.admin;

import com.cocciahouse.api.dto.menu.PizzaToppingRequest;
import com.cocciahouse.api.dto.menu.PizzaToppingResponse;
import com.cocciahouse.api.service.PizzaToppingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(
        "/api/admin/menu/sections/{menuSectionId}/pizza/toppings"
)
public class PizzaToppingAdminController {

    private final PizzaToppingService pizzaToppingService;

    public PizzaToppingAdminController(
            PizzaToppingService pizzaToppingService
    ) {
        this.pizzaToppingService = pizzaToppingService;
    }

    @GetMapping
    public List<PizzaToppingResponse> getToppings(
            @PathVariable Long menuSectionId
    ) {
        return pizzaToppingService.getToppings(
                menuSectionId
        );
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PizzaToppingResponse createTopping(
            @PathVariable Long menuSectionId,
            @Valid @RequestBody PizzaToppingRequest request
    ) {
        return pizzaToppingService.createTopping(
                menuSectionId,
                request
        );
    }

    @PutMapping("/{pizzaToppingId}")
    public PizzaToppingResponse updateTopping(
            @PathVariable Long menuSectionId,
            @PathVariable Long pizzaToppingId,
            @Valid @RequestBody PizzaToppingRequest request
    ) {
        return pizzaToppingService.updateTopping(
                menuSectionId,
                pizzaToppingId,
                request
        );
    }
}