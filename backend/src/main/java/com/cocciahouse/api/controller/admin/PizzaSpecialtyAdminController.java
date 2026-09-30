package com.cocciahouse.api.controller.admin;

import com.cocciahouse.api.dto.menu.PizzaSpecialtyRequest;
import com.cocciahouse.api.dto.menu.PizzaSpecialtyResponse;
import com.cocciahouse.api.service.PizzaSpecialtyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(
        "/api/admin/menu/sections/{menuSectionId}/pizza/specialties"
)
public class PizzaSpecialtyAdminController {

    private final PizzaSpecialtyService pizzaSpecialtyService;

    public PizzaSpecialtyAdminController(
            PizzaSpecialtyService pizzaSpecialtyService
    ) {
        this.pizzaSpecialtyService =
                pizzaSpecialtyService;
    }

    @GetMapping
    public List<PizzaSpecialtyResponse> getSpecialties(
            @PathVariable Long menuSectionId
    ) {
        return pizzaSpecialtyService.getSpecialties(
                menuSectionId
        );
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PizzaSpecialtyResponse createSpecialty(
            @PathVariable Long menuSectionId,
            @Valid @RequestBody PizzaSpecialtyRequest request
    ) {
        return pizzaSpecialtyService.createSpecialty(
                menuSectionId,
                request
        );
    }

    @PutMapping("/{specialtyId}")
    public PizzaSpecialtyResponse updateSpecialty(
            @PathVariable Long menuSectionId,
            @PathVariable Long specialtyId,
            @Valid @RequestBody PizzaSpecialtyRequest request
    ) {
        return pizzaSpecialtyService.updateSpecialty(
                menuSectionId,
                specialtyId,
                request
        );
    }
}