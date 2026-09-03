package com.cocciahouse.api.controller.admin;

import com.cocciahouse.api.dto.menu.MenuItemPriceResponse;
import com.cocciahouse.api.dto.menu.MenuItemRequest;
import com.cocciahouse.api.dto.menu.MenuItemResponse;
import com.cocciahouse.api.model.MenuItem;
import com.cocciahouse.api.model.MenuItemPrice;
import com.cocciahouse.api.service.MenuItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/admin/menu/sections/{menuSectionId}/items")
public class AdminMenuItemController {

    private final MenuItemService menuItemService;

    public AdminMenuItemController(
            MenuItemService menuItemService
    ) {
        this.menuItemService =
                menuItemService;
    }

    @GetMapping
    public List<MenuItemResponse> getMenuItems(
            @PathVariable Long menuSectionId
    ) {
        return menuItemService
                .getMenuItemsForSection(
                        menuSectionId
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MenuItemResponse createMenuItem(
            @PathVariable Long menuSectionId,
            @Valid @RequestBody
            MenuItemRequest request
    ) {
        MenuItem menuItem =
                menuItemService.createMenuItem(
                        menuSectionId,
                        request
                );

        return toResponse(menuItem);
    }

    @PutMapping("/{menuItemId}")
    public MenuItemResponse updateMenuItem(
            @PathVariable Long menuSectionId,
            @PathVariable Long menuItemId,
            @Valid @RequestBody
            MenuItemRequest request
    ) {
        MenuItem menuItem =
                menuItemService.updateMenuItem(
                        menuSectionId,
                        menuItemId,
                        request
                );

        return toResponse(menuItem);
    }

    private MenuItemResponse toResponse(
            MenuItem menuItem
    ) {
        return new MenuItemResponse(
                menuItem.getId(),
                menuItem
                        .getMenuSection()
                        .getId(),
                menuItem
                        .getRecipe()
                        .getId(),
                menuItem
                        .getRecipe()
                        .getName(),
                menuItem
                        .getRecipe()
                        .getDescription(),
                menuItem
                        .getRecipe()
                        .getImageUrl(),
                menuItem
                        .getRecipe()
                        .getImageAlt(),
                menuItem.getDisplayOrder(),
                menuItem.isVisible(),
                menuItem
                        .getPrices()
                        .stream()
                        .sorted(
                                Comparator.comparingInt(
                                        MenuItemPrice::getDisplayOrder
                                )
                        )
                        .map(
                                price ->
                                        new MenuItemPriceResponse(
                                                price.getId(),
                                                price.getLabel(),
                                                price.getAmount(),
                                                price.getDisplayOrder()
                                        )
                        )
                        .toList()
        );
    }
}
