package com.cocciahouse.api.controller.admin;

import com.cocciahouse.api.dto.menu.PizzaAddOnRequest;
import com.cocciahouse.api.dto.menu.PizzaAddOnResponse;
import com.cocciahouse.api.service.PizzaAddOnService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(
        "/api/admin/menu/sections/{menuSectionId}/pizza/add-ons"
)
public class PizzaAddOnAdminController {

    private final PizzaAddOnService pizzaAddOnService;

    public PizzaAddOnAdminController(
            PizzaAddOnService pizzaAddOnService
    ) {
        this.pizzaAddOnService = pizzaAddOnService;
    }

    @GetMapping
    public List<PizzaAddOnResponse> getAddOns(
            @PathVariable Long menuSectionId
    ) {
        return pizzaAddOnService.getAddOns(
                menuSectionId
        );
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PizzaAddOnResponse createAddOn(
            @PathVariable Long menuSectionId,
            @Valid @RequestBody PizzaAddOnRequest request
    ) {
        return pizzaAddOnService.createAddOn(
                menuSectionId,
                request
        );
    }

    @PutMapping("/{pizzaAddOnId}")
    public PizzaAddOnResponse updateAddOn(
            @PathVariable Long menuSectionId,
            @PathVariable Long pizzaAddOnId,
            @Valid @RequestBody PizzaAddOnRequest request
    ) {
        return pizzaAddOnService.updateAddOn(
                menuSectionId,
                pizzaAddOnId,
                request
        );
    }
}