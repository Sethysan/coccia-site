package com.cocciahouse.api.service;

import com.cocciahouse.api.dto.menu.MenuItemPriceRequest;
import com.cocciahouse.api.dto.menu.MenuItemRequest;
import com.cocciahouse.api.model.MenuItem;
import com.cocciahouse.api.model.MenuItemPrice;
import com.cocciahouse.api.model.MenuSection;
import com.cocciahouse.api.model.Recipe;
import com.cocciahouse.api.repository.MenuItemRepository;
import com.cocciahouse.api.repository.MenuSectionRepository;
import com.cocciahouse.api.repository.RecipeRepository;
import com.cocciahouse.api.exception.MenuItemNotFoundException;
import com.cocciahouse.api.exception.MenuSectionNotFoundException;
import com.cocciahouse.api.exception.RecipeNotFoundException;
import com.cocciahouse.api.exception.DuplicateMenuItemException;
import com.cocciahouse.api.dto.menu.MenuItemMoveDirection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;

@Service
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final MenuSectionRepository menuSectionRepository;
    private final RecipeRepository recipeRepository;

    public MenuItemService(
            MenuItemRepository menuItemRepository,
            MenuSectionRepository menuSectionRepository,
            RecipeRepository recipeRepository
    ) {
        this.menuItemRepository = menuItemRepository;
        this.menuSectionRepository = menuSectionRepository;
        this.recipeRepository = recipeRepository;
    }

    @Transactional(readOnly = true)
    public List<MenuItem> getMenuItemsForSection(
            Long menuSectionId
    ) {
        return menuItemRepository
                .findByMenuSectionIdOrderByDisplayOrderAsc(
                        menuSectionId
                );
    }

    @Transactional(readOnly = true)
    public List<MenuItem> getVisibleMenuItemsForSection(
            Long menuSectionId
    ) {
        return menuItemRepository
                .findByMenuSectionIdAndVisibleTrueOrderByDisplayOrderAsc(
                        menuSectionId
                );
    }

    @Transactional
    public MenuItem createMenuItem(
            Long menuSectionId,
            MenuItemRequest request
    ) {

        MenuSection section =
                menuSectionRepository
                        .findById(menuSectionId)
                        .orElseThrow(() ->
                                new MenuSectionNotFoundException(
                                        "Menu section not found."
                                )
                        );

        Recipe recipe =
                recipeRepository
                        .findById(request.recipeId())
                        .orElseThrow(() ->
                                new RecipeNotFoundException(
                                        "Recipe not found."
                                )
                        );

        if (!recipe.isActive()) {
            throw new IllegalArgumentException(
                    "Inactive recipes cannot be added to the menu."
            );
        }

        if (
                menuItemRepository
                        .existsByMenuSectionIdAndRecipeId(
                                menuSectionId,
                                recipe.getId()
                        )
        ) {
            throw new DuplicateMenuItemException(
                    "That recipe is already in this menu section."
            );
        }

        MenuItem menuItem =
                new MenuItem();

        menuItem.setMenuSection(section);
        menuItem.setRecipe(recipe);
        List<MenuItem> existingItems =
                menuItemRepository
                        .findByMenuSectionIdOrderByDisplayOrderAsc(
                                menuSectionId
                        );

        int nextDisplayOrder =
                existingItems.stream()
                        .mapToInt(MenuItem::getDisplayOrder)
                        .max()
                        .orElse(-1)
                        + 1;

        menuItem.setDisplayOrder(
                nextDisplayOrder
        );
        menuItem.setVisible(
                request.visible()
        );

        for (
                MenuItemPriceRequest priceRequest
                : request.prices()
        ) {

            MenuItemPrice price =
                    new MenuItemPrice();

            price.setLabel(
                    cleanNullableText(
                            priceRequest.label()
                    )
            );

            price.setAmount(
                    priceRequest.amount()
            );

            price.setDisplayOrder(
                    priceRequest.displayOrder()
            );

            menuItem.addPrice(price);
        }

        return menuItemRepository.save(menuItem);
    }

    @Transactional
    public MenuItem updateMenuItem(
            Long menuSectionId,
            Long menuItemId,
            MenuItemRequest request
    ) {

        MenuItem menuItem =
                menuItemRepository
                        .findByIdAndMenuSectionId(
                                menuItemId,
                                menuSectionId
                        )
                        .orElseThrow(() ->
                                new MenuItemNotFoundException(
                                        "Menu item does not belong to that menu section."
                                )
                        );

        Recipe recipe =
                recipeRepository
                        .findById(request.recipeId())
                        .orElseThrow(() ->
                                new RecipeNotFoundException(
                                        "Recipe not found."
                                )
                        );

        boolean recipeChanged =
                !request.recipeId().equals(
                        menuItem.getRecipe().getId()
                );

        if (
                recipeChanged
                        && !recipe.isActive()
        ) {
            throw new IllegalArgumentException(
                    "Inactive recipes cannot be added to the menu."
            );
        }

        if (
                menuItemRepository
                        .existsByMenuSectionIdAndRecipeIdAndIdNot(
                                menuSectionId,
                                recipe.getId(),
                                menuItemId
                        )
        ) {
            throw new DuplicateMenuItemException(
                    "That recipe is already in this menu section."
            );
        }

        menuItem.setRecipe(recipe);

        menuItem.setDisplayOrder(
                request.displayOrder()
        );

        menuItem.setVisible(
                request.visible()
        );

        menuItem.clearPrices();

        for (
                MenuItemPriceRequest priceRequest
                : request.prices()
        ) {

            MenuItemPrice price =
                    new MenuItemPrice();

            price.setLabel(
                    cleanNullableText(
                            priceRequest.label()
                    )
            );

            price.setAmount(
                    priceRequest.amount()
            );

            price.setDisplayOrder(
                    priceRequest.displayOrder()
            );

            menuItem.addPrice(price);
        }

        return menuItemRepository.save(menuItem);
    }

    private String cleanNullableText(
            String value
    ) {

        if (value == null) {
            return null;
        }

        String cleaned =
                value.trim();

        return cleaned.isEmpty()
                ? null
                : cleaned;
    }

    @Transactional
    public List<MenuItem> moveMenuItem(
            Long menuSectionId,
            Long menuItemId,
            MenuItemMoveDirection direction
    ) {
        if (direction == null) {
            throw new IllegalArgumentException(
                    "Move direction is required."
            );
        }

        List<MenuItem> items =
                new ArrayList<>(
                        menuItemRepository
                                .findByMenuSectionIdOrderByDisplayOrderAsc(
                                        menuSectionId
                                )
                );

        int currentIndex = -1;

        for (int index = 0; index < items.size(); index++) {
            if (items.get(index).getId().equals(menuItemId)) {
                currentIndex = index;
                break;
            }
        }

        if (currentIndex == -1) {
            throw new MenuItemNotFoundException(
                    "Menu item not found in that menu section."
            );
        }

        int targetIndex =
                direction == MenuItemMoveDirection.UP
                        ? currentIndex - 1
                        : currentIndex + 1;

        if (targetIndex < 0 || targetIndex >= items.size()) {
            return items;
        }

        MenuItem currentItem = items.get(currentIndex);

        items.remove(currentIndex);
        items.add(targetIndex, currentItem);

        for (int index = 0; index < items.size(); index++) {
            MenuItem item = items.get(index);

            item.setDisplayOrder(index);
            menuItemRepository.save(item);
        }

        return menuItemRepository
                .findByMenuSectionIdOrderByDisplayOrderAsc(
                        menuSectionId
                );
    }

}