package com.cocciahouse.api.controller.admin;

import com.cocciahouse.api.dto.menu.MenuItemResponse;
import com.cocciahouse.api.model.MenuItem;
import com.cocciahouse.api.model.MenuItemPrice;
import com.cocciahouse.api.model.MenuSection;
import com.cocciahouse.api.model.Recipe;
import com.cocciahouse.api.service.MenuItemService;
import com.cocciahouse.api.dto.menu.MenuItemPriceRequest;
import com.cocciahouse.api.dto.menu.MenuItemRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminMenuItemControllerTest {

    private MenuItemService menuItemService;
    private AdminMenuItemController controller;

    @BeforeEach
    void setUp() {
        menuItemService =
                mock(MenuItemService.class);

        controller =
                new AdminMenuItemController(
                        menuItemService
                );
    }

    @Test
    void getMenuItems_mapsRecipeAndPrices() {

        Long sectionId = 2L;

        MenuSection section =
                mock(MenuSection.class);

        Recipe recipe =
                mock(Recipe.class);

        when(
                section.getId()
        ).thenReturn(sectionId);

        when(
                recipe.getId()
        ).thenReturn(10L);

        when(
                recipe.getName()
        ).thenReturn("Cold Antipasto");

        when(
                recipe.getDescription()
        ).thenReturn("Italian meats, cheese and vegetables.");

        when(
                recipe.getImageUrl()
        ).thenReturn("https://example.com/antipasto.jpg");

        when(
                recipe.getImageAlt()
        ).thenReturn("Cold antipasto salad");

        MenuItem menuItem =
                new MenuItem();

        menuItem.setMenuSection(section);
        menuItem.setRecipe(recipe);
        menuItem.setDisplayOrder(3);
        menuItem.setVisible(true);

        MenuItemPrice large =
                new MenuItemPrice();

        large.setLabel("Large");
        large.setAmount(
                new BigDecimal("20.00")
        );
        large.setDisplayOrder(1);

        MenuItemPrice regular =
                new MenuItemPrice();

        regular.setLabel("Regular");
        regular.setAmount(
                new BigDecimal("16.00")
        );
        regular.setDisplayOrder(0);

        // Add these backwards on purpose.
        // The controller should sort by displayOrder.
        menuItem.addPrice(large);
        menuItem.addPrice(regular);

        when(
                menuItemService
                        .getMenuItemsForSection(
                                sectionId
                        )
        ).thenReturn(
                List.of(menuItem)
        );

        List<MenuItemResponse> result =
                controller.getMenuItems(
                        sectionId
                );

        assertEquals(
                1,
                result.size()
        );

        MenuItemResponse response =
                result.getFirst();

        assertEquals(
                sectionId,
                response.menuSectionId()
        );

        assertEquals(
                10L,
                response.recipeId()
        );

        assertEquals(
                "Cold Antipasto",
                response.recipeName()
        );

        assertEquals(
                "Italian meats, cheese and vegetables.",
                response.description()
        );

        assertEquals(
                "https://example.com/antipasto.jpg",
                response.imageUrl()
        );

        assertEquals(
                "Cold antipasto salad",
                response.imageAlt()
        );

        assertEquals(
                3,
                response.displayOrder()
        );

        assertTrue(
                response.visible()
        );

        assertEquals(
                2,
                response.prices().size()
        );

        assertEquals(
                "Regular",
                response.prices().get(0).label()
        );

        assertEquals(
                new BigDecimal("16.00"),
                response.prices().get(0).amount()
        );

        assertEquals(
                "Large",
                response.prices().get(1).label()
        );

        assertEquals(
                new BigDecimal("20.00"),
                response.prices().get(1).amount()
        );
    }

    @Test
    void createMenuItem_callsServiceAndMapsResponse() {

        Long sectionId = 2L;

        MenuItemRequest request =
                new MenuItemRequest(
                        10L,
                        3,
                        true,
                        List.of(
                                new MenuItemPriceRequest(
                                        null,
                                        new BigDecimal("16.00"),
                                        0
                                )
                        )
                );

        MenuSection section =
                mock(MenuSection.class);

        Recipe recipe =
                mock(Recipe.class);

        when(
                section.getId()
        ).thenReturn(sectionId);

        when(
                recipe.getId()
        ).thenReturn(10L);

        when(
                recipe.getName()
        ).thenReturn("Chicken Parmesan");

        MenuItem menuItem =
                new MenuItem();

        menuItem.setMenuSection(section);
        menuItem.setRecipe(recipe);
        menuItem.setDisplayOrder(3);
        menuItem.setVisible(true);

        MenuItemPrice price =
                new MenuItemPrice();

        price.setAmount(
                new BigDecimal("16.00")
        );

        price.setDisplayOrder(0);

        menuItem.addPrice(price);

        when(
                menuItemService.createMenuItem(
                        sectionId,
                        request
                )
        ).thenReturn(menuItem);

        MenuItemResponse response =
                controller.createMenuItem(
                        sectionId,
                        request
                );

        assertEquals(
                sectionId,
                response.menuSectionId()
        );

        assertEquals(
                10L,
                response.recipeId()
        );

        assertEquals(
                "Chicken Parmesan",
                response.recipeName()
        );

        assertEquals(
                3,
                response.displayOrder()
        );

        assertTrue(
                response.visible()
        );

        assertEquals(
                new BigDecimal("16.00"),
                response.prices().getFirst().amount()
        );

        verify(
                menuItemService
        ).createMenuItem(
                sectionId,
                request
        );
    }

    @Test
    void updateMenuItem_callsServiceAndMapsResponse() {

        Long sectionId = 2L;
        Long menuItemId = 20L;

        MenuItemRequest request =
                new MenuItemRequest(
                        10L,
                        5,
                        false,
                        List.of(
                                new MenuItemPriceRequest(
                                        "Large",
                                        new BigDecimal("21.00"),
                                        0
                                )
                        )
                );

        MenuSection section =
                mock(MenuSection.class);

        Recipe recipe =
                mock(Recipe.class);

        when(
                section.getId()
        ).thenReturn(sectionId);

        when(
                recipe.getId()
        ).thenReturn(10L);

        when(
                recipe.getName()
        ).thenReturn("Cold Antipasto");

        MenuItem menuItem =
                new MenuItem();

        menuItem.setMenuSection(section);
        menuItem.setRecipe(recipe);
        menuItem.setDisplayOrder(5);
        menuItem.setVisible(false);

        MenuItemPrice price =
                new MenuItemPrice();

        price.setLabel("Large");

        price.setAmount(
                new BigDecimal("21.00")
        );

        price.setDisplayOrder(0);

        menuItem.addPrice(price);

        when(
                menuItemService.updateMenuItem(
                        sectionId,
                        menuItemId,
                        request
                )
        ).thenReturn(menuItem);

        MenuItemResponse response =
                controller.updateMenuItem(
                        sectionId,
                        menuItemId,
                        request
                );

        assertEquals(
                sectionId,
                response.menuSectionId()
        );

        assertEquals(
                10L,
                response.recipeId()
        );

        assertEquals(
                "Cold Antipasto",
                response.recipeName()
        );

        assertEquals(
                5,
                response.displayOrder()
        );

        assertFalse(
                response.visible()
        );

        assertEquals(
                "Large",
                response.prices().getFirst().label()
        );

        assertEquals(
                new BigDecimal("21.00"),
                response.prices().getFirst().amount()
        );

        verify(
                menuItemService
        ).updateMenuItem(
                sectionId,
                menuItemId,
                request
        );
    }

}