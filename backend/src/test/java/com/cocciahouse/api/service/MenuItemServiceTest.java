package com.cocciahouse.api.service;

import com.cocciahouse.api.dto.menu.MenuItemPriceRequest;
import com.cocciahouse.api.dto.menu.MenuItemRequest;
import com.cocciahouse.api.model.MenuItem;
import com.cocciahouse.api.model.MenuSection;
import com.cocciahouse.api.model.Recipe;
import com.cocciahouse.api.repository.MenuItemRepository;
import com.cocciahouse.api.repository.MenuSectionRepository;
import com.cocciahouse.api.repository.RecipeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuItemServiceTest {

    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private MenuSectionRepository menuSectionRepository;

    @Mock
    private RecipeRepository recipeRepository;

    private MenuItemService menuItemService;

    @BeforeEach
    void setUp() {
        menuItemService =
                new MenuItemService(
                        menuItemRepository,
                        menuSectionRepository,
                        recipeRepository
                );
    }

    @Test
    void createMenuItem_createsItemWithOrderedPrices() {

        Long sectionId = 2L;
        Long recipeId = 10L;

        MenuSection section =
                new MenuSection("Salads");

        Recipe recipe =
                new Recipe("Cold Antipasto");

        recipe.setActive(true);

        MenuItemRequest request =
                new MenuItemRequest(
                        recipeId,
                        3,
                        true,
                        List.of(
                                new MenuItemPriceRequest(
                                        "  Regular  ",
                                        new BigDecimal("16.00"),
                                        0
                                ),
                                new MenuItemPriceRequest(
                                        "Large",
                                        new BigDecimal("20.00"),
                                        1
                                )
                        )
                );

        when(
                menuSectionRepository.findById(sectionId)
        ).thenReturn(
                Optional.of(section)
        );

        when(
                recipeRepository.findById(recipeId)
        ).thenReturn(
                Optional.of(recipe)
        );

        when(
                menuItemRepository
                        .existsByMenuSectionIdAndRecipeId(
                                sectionId,
                                recipe.getId()
                        )
        ).thenReturn(false);

        when(
                menuItemRepository.save(any(MenuItem.class))
        ).thenAnswer(
                invocation -> invocation.getArgument(0)
        );

        MenuItem result =
                menuItemService.createMenuItem(
                        sectionId,
                        request
                );

        assertSame(
                section,
                result.getMenuSection()
        );

        assertSame(
                recipe,
                result.getRecipe()
        );

        assertEquals(
                3,
                result.getDisplayOrder()
        );

        assertTrue(
                result.isVisible()
        );

        assertEquals(
                2,
                result.getPrices().size()
        );

        assertEquals(
                "Regular",
                result.getPrices().get(0).getLabel()
        );

        assertEquals(
                new BigDecimal("16.00"),
                result.getPrices().get(0).getAmount()
        );

        assertEquals(
                "Large",
                result.getPrices().get(1).getLabel()
        );

        assertSame(
                result,
                result.getPrices().get(0).getMenuItem()
        );

        assertSame(
                result,
                result.getPrices().get(1).getMenuItem()
        );
    }

    @Test
    void createMenuItem_throwsWhenSectionDoesNotExist() {

        Long sectionId = 999L;

        MenuItemRequest request =
                new MenuItemRequest(
                        10L,
                        0,
                        true,
                        List.of(
                                new MenuItemPriceRequest(
                                        null,
                                        new BigDecimal("10.00"),
                                        0
                                )
                        )
                );

        when(
                menuSectionRepository.findById(sectionId)
        ).thenReturn(
                Optional.empty()
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                menuItemService.createMenuItem(
                                        sectionId,
                                        request
                                )
                );

        assertEquals(
                "Menu section not found.",
                exception.getMessage()
        );

        verify(
                recipeRepository,
                never()
        ).findById(any());

        verify(
                menuItemRepository,
                never()
        ).save(any());
    }

    @Test
    void createMenuItem_throwsWhenRecipeDoesNotExist() {

        Long sectionId = 2L;
        Long recipeId = 999L;

        MenuSection section =
                new MenuSection("Salads");

        MenuItemRequest request =
                new MenuItemRequest(
                        recipeId,
                        0,
                        true,
                        List.of(
                                new MenuItemPriceRequest(
                                        null,
                                        new BigDecimal("10.00"),
                                        0
                                )
                        )
                );

        when(
                menuSectionRepository.findById(sectionId)
        ).thenReturn(
                Optional.of(section)
        );

        when(
                recipeRepository.findById(recipeId)
        ).thenReturn(
                Optional.empty()
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                menuItemService.createMenuItem(
                                        sectionId,
                                        request
                                )
                );

        assertEquals(
                "Recipe not found.",
                exception.getMessage()
        );

        verify(
                menuItemRepository,
                never()
        ).save(any());
    }

    @Test
    void createMenuItem_throwsWhenRecipeIsInactive() {

        Long sectionId = 2L;
        Long recipeId = 10L;

        MenuSection section =
                new MenuSection("Salads");

        Recipe recipe =
                new Recipe("Cold Antipasto");

        recipe.setActive(false);

        MenuItemRequest request =
                new MenuItemRequest(
                        recipeId,
                        0,
                        true,
                        List.of(
                                new MenuItemPriceRequest(
                                        null,
                                        new BigDecimal("16.00"),
                                        0
                                )
                        )
                );

        when(
                menuSectionRepository.findById(sectionId)
        ).thenReturn(
                Optional.of(section)
        );

        when(
                recipeRepository.findById(recipeId)
        ).thenReturn(
                Optional.of(recipe)
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                menuItemService.createMenuItem(
                                        sectionId,
                                        request
                                )
                );

        assertEquals(
                "Inactive recipes cannot be added to the menu.",
                exception.getMessage()
        );

        verify(
                menuItemRepository,
                never()
        ).save(any());
    }

    @Test
    void createMenuItem_throwsWhenRecipeAlreadyExistsInSection() {

        Long sectionId = 2L;
        Long recipeId = 10L;

        MenuSection section =
                new MenuSection("Salads");

        Recipe recipe =
                new Recipe("Cold Antipasto");

        recipe.setActive(true);

        MenuItemRequest request =
                new MenuItemRequest(
                        recipeId,
                        0,
                        true,
                        List.of(
                                new MenuItemPriceRequest(
                                        null,
                                        new BigDecimal("16.00"),
                                        0
                                )
                        )
                );

        when(
                menuSectionRepository.findById(sectionId)
        ).thenReturn(
                Optional.of(section)
        );

        when(
                recipeRepository.findById(recipeId)
        ).thenReturn(
                Optional.of(recipe)
        );

        when(
                menuItemRepository
                        .existsByMenuSectionIdAndRecipeId(
                                sectionId,
                                recipe.getId()
                        )
        ).thenReturn(true);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                menuItemService.createMenuItem(
                                        sectionId,
                                        request
                                )
                );

        assertEquals(
                "That recipe is already in this menu section.",
                exception.getMessage()
        );

        verify(
                menuItemRepository,
                never()
        ).save(any());
    }

    @Test
    void updateMenuItem_updatesRecipeVisibilityOrderAndPrices() {

        Long sectionId = 2L;
        Long menuItemId = 20L;
        Long oldRecipeId = 10L;
        Long newRecipeId = 11L;

        MenuSection section =
                new MenuSection("Salads");

        Recipe oldRecipe =
                mock(Recipe.class);

        Recipe newRecipe =
                mock(Recipe.class);

        when(
                oldRecipe.getId()
        ).thenReturn(oldRecipeId);

        when(
                newRecipe.getId()
        ).thenReturn(newRecipeId);

        when(
                newRecipe.isActive()
        ).thenReturn(true);

        MenuItem existing =
                new MenuItem();

        existing.setMenuSection(section);
        existing.setRecipe(oldRecipe);
        existing.setDisplayOrder(1);
        existing.setVisible(true);

        MenuItemRequest request =
                new MenuItemRequest(
                        newRecipeId,
                        3,
                        false,
                        List.of(
                                new MenuItemPriceRequest(
                                        " Regular ",
                                        new BigDecimal("17.00"),
                                        0
                                ),
                                new MenuItemPriceRequest(
                                        " Large ",
                                        new BigDecimal("21.00"),
                                        1
                                )
                        )
                );

        when(
                menuItemRepository.findByIdAndMenuSectionId(
                        menuItemId,
                        sectionId
                )
        ).thenReturn(
                Optional.of(existing)
        );

        when(
                recipeRepository.findById(newRecipeId)
        ).thenReturn(
                Optional.of(newRecipe)
        );

        when(
                menuItemRepository
                        .existsByMenuSectionIdAndRecipeIdAndIdNot(
                                sectionId,
                                newRecipe.getId(),
                                menuItemId
                        )
        ).thenReturn(false);

        when(
                menuItemRepository.save(existing)
        ).thenReturn(existing);

        MenuItem result =
                menuItemService.updateMenuItem(
                        sectionId,
                        menuItemId,
                        request
                );

        assertSame(
                newRecipe,
                result.getRecipe()
        );

        assertEquals(
                3,
                result.getDisplayOrder()
        );

        assertFalse(
                result.isVisible()
        );

        assertEquals(
                2,
                result.getPrices().size()
        );

        assertEquals(
                "Regular",
                result.getPrices().get(0).getLabel()
        );

        assertEquals(
                new BigDecimal("17.00"),
                result.getPrices().get(0).getAmount()
        );

        assertEquals(
                "Large",
                result.getPrices().get(1).getLabel()
        );

        assertSame(
                result,
                result.getPrices().get(0).getMenuItem()
        );
    }

    @Test
    void updateMenuItem_allowsEditingWhenExistingRecipeIsInactive() {

        Long sectionId = 2L;
        Long menuItemId = 20L;
        Long recipeId = 10L;

        MenuSection section =
                new MenuSection("Entrees");

        Recipe recipe =
                mock(Recipe.class);

        when(
                recipe.getId()
        ).thenReturn(recipeId);

        MenuItem existing =
                new MenuItem();

        existing.setMenuSection(section);
        existing.setRecipe(recipe);
        existing.setVisible(true);

        MenuItemRequest request =
                new MenuItemRequest(
                        recipeId,
                        4,
                        false,
                        List.of(
                                new MenuItemPriceRequest(
                                        null,
                                        new BigDecimal("22.00"),
                                        0
                                )
                        )
                );

        when(
                menuItemRepository.findByIdAndMenuSectionId(
                        menuItemId,
                        sectionId
                )
        ).thenReturn(
                Optional.of(existing)
        );

        when(
                recipeRepository.findById(recipeId)
        ).thenReturn(
                Optional.of(recipe)
        );

        when(
                menuItemRepository
                        .existsByMenuSectionIdAndRecipeIdAndIdNot(
                                sectionId,
                                recipeId,
                                menuItemId
                        )
        ).thenReturn(false);

        when(
                menuItemRepository.save(existing)
        ).thenReturn(existing);

        MenuItem result =
                menuItemService.updateMenuItem(
                        sectionId,
                        menuItemId,
                        request
                );

        assertSame(
                recipe,
                result.getRecipe()
        );

        assertFalse(
                result.isVisible()
        );

        assertEquals(
                new BigDecimal("22.00"),
                result.getPrices().getFirst().getAmount()
        );
    }

    @Test
    void updateMenuItem_rejectsChangingToInactiveRecipe() {

        Long sectionId = 2L;
        Long menuItemId = 20L;
        Long oldRecipeId = 10L;
        Long inactiveRecipeId = 11L;

        MenuSection section =
                new MenuSection("Entrees");

        Recipe oldRecipe =
                mock(Recipe.class);

        Recipe inactiveRecipe =
                mock(Recipe.class);

        when(
                oldRecipe.getId()
        ).thenReturn(oldRecipeId);

        when(
                inactiveRecipe.isActive()
        ).thenReturn(false);

        MenuItem existing =
                new MenuItem();

        existing.setMenuSection(section);
        existing.setRecipe(oldRecipe);

        MenuItemRequest request =
                new MenuItemRequest(
                        inactiveRecipeId,
                        4,
                        true,
                        List.of(
                                new MenuItemPriceRequest(
                                        null,
                                        new BigDecimal("22.00"),
                                        0
                                )
                        )
                );

        when(
                menuItemRepository.findByIdAndMenuSectionId(
                        menuItemId,
                        sectionId
                )
        ).thenReturn(
                Optional.of(existing)
        );

        when(
                recipeRepository.findById(inactiveRecipeId)
        ).thenReturn(
                Optional.of(inactiveRecipe)
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                menuItemService.updateMenuItem(
                                        sectionId,
                                        menuItemId,
                                        request
                                )
                );

        assertEquals(
                "Inactive recipes cannot be added to the menu.",
                exception.getMessage()
        );

        verify(
                menuItemRepository,
                never()
        ).save(any());
    }

    @Test
    void updateMenuItem_rejectsItemFromDifferentSection() {

        Long requestedSectionId = 3L;
        Long menuItemId = 20L;

        MenuSection actualSection =
                new MenuSection("Entrees");

        Recipe recipe =
                new Recipe("Chicken Parmesan");

        MenuItem existing =
                new MenuItem();

        existing.setMenuSection(actualSection);
        existing.setRecipe(recipe);

        MenuItemRequest request =
                new MenuItemRequest(
                        10L,
                        4,
                        true,
                        List.of(
                                new MenuItemPriceRequest(
                                        null,
                                        new BigDecimal("21.00"),
                                        0
                                )
                        )
                );

        when(
                menuItemRepository.findByIdAndMenuSectionId(
                        menuItemId,
                        requestedSectionId
                )
        ).thenReturn(
                Optional.empty()
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                menuItemService.updateMenuItem(
                                        requestedSectionId,
                                        menuItemId,
                                        request
                                )
                );

        assertEquals(
                "Menu item does not belong to that menu section.",
                exception.getMessage()
        );

        verify(
                recipeRepository,
                never()
        ).findById(any());

        verify(
                menuItemRepository,
                never()
        ).save(any());
    }

}