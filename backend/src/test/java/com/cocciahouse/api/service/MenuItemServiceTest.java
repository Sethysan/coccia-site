package com.cocciahouse.api.service;

import com.cocciahouse.api.dto.menu.MenuItemPriceRequest;
import com.cocciahouse.api.dto.menu.MenuItemRequest;
import com.cocciahouse.api.model.MenuItem;
import com.cocciahouse.api.model.MenuSection;
import com.cocciahouse.api.model.Recipe;
import com.cocciahouse.api.model.MenuSubsection;
import com.cocciahouse.api.repository.MenuItemRepository;
import com.cocciahouse.api.repository.MenuSectionRepository;
import com.cocciahouse.api.repository.RecipeRepository;
import com.cocciahouse.api.repository.MenuSubsectionRepository;
import com.cocciahouse.api.exception.MenuItemNotFoundException;
import com.cocciahouse.api.exception.MenuSectionNotFoundException;
import com.cocciahouse.api.exception.RecipeNotFoundException;
import com.cocciahouse.api.exception.DuplicateMenuItemException;
import com.cocciahouse.api.dto.menu.MenuItemMoveDirection;
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

    @Mock
    private MenuSubsectionRepository menuSubsectionRepository;

    private MenuItemService menuItemService;

    @BeforeEach
    void setUp() {
        menuItemService =
                new MenuItemService(
                        menuItemRepository,
                        menuSectionRepository,
                        recipeRepository,
                        menuSubsectionRepository
                );
    }

    @Test
    void getMenuItemsForSection_returnsItemsInRepositoryOrder() {

        Long sectionId = 2L;

        MenuItem first =
                new MenuItem();

        MenuItem second =
                new MenuItem();

        List<MenuItem> items =
                List.of(
                        first,
                        second
                );

        when(
                menuItemRepository
                        .findByMenuSectionIdOrderByDisplayOrderAsc(
                                sectionId
                        )
        ).thenReturn(items);

        List<MenuItem> result =
                menuItemService.getMenuItemsForSection(
                        sectionId
                );

        assertEquals(
                items,
                result
        );
    }

    @Test
    void getVisibleMenuItemsForSection_returnsVisibleItemsInRepositoryOrder() {

        Long sectionId = 2L;

        MenuItem first =
                new MenuItem();

        MenuItem second =
                new MenuItem();

        List<MenuItem> items =
                List.of(
                        first,
                        second
                );

        when(
                menuItemRepository
                        .findByMenuSectionIdAndVisibleTrueOrderByDisplayOrderAsc(
                                sectionId
                        )
        ).thenReturn(items);

        List<MenuItem> result =
                menuItemService
                        .getVisibleMenuItemsForSection(
                                sectionId
                        );

        assertEquals(
                items,
                result
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

        MenuItem existingItem =
                mock(MenuItem.class);

        when(
                existingItem.getDisplayOrder()
        ).thenReturn(4);

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
                menuItemRepository
                        .findByMenuSectionIdOrderByDisplayOrderAsc(
                                sectionId
                        )
        ).thenReturn(
                List.of(existingItem)
        );

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
                5,
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
    void createMenuItem_assignsValidSubsection() {

        Long sectionId = 2L;
        Long subsectionId = 5L;
        Long recipeId = 10L;

        MenuSection section =
                new MenuSection("Beverages");

        MenuSubsection subsection =
                new MenuSubsection(
                        section,
                        "Beer"
                );

        Recipe recipe =
                new Recipe("Bud Light");

        recipe.setActive(true);

        MenuItemRequest request =
                new MenuItemRequest(
                        recipeId,
                        subsectionId,
                        0,
                        true,
                        List.of(
                                new MenuItemPriceRequest(
                                        null,
                                        new BigDecimal("4.00"),
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
        ).thenReturn(false);

        when(
                menuSubsectionRepository
                        .findByIdAndMenuSectionId(
                                subsectionId,
                                sectionId
                        )
        ).thenReturn(
                Optional.of(subsection)
        );

        when(
                menuItemRepository
                        .findByMenuSectionIdOrderByDisplayOrderAsc(
                                sectionId
                        )
        ).thenReturn(
                List.of()
        );

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

        assertSame(
                subsection,
                result.getMenuSubsection()
        );

        assertEquals(
                0,
                result.getDisplayOrder()
        );

        assertTrue(
                result.isVisible()
        );
    }

    @Test
    void createMenuItem_throwsWhenSubsectionBelongsToDifferentSection() {

        Long sectionId = 2L;
        Long subsectionId = 5L;
        Long recipeId = 10L;

        MenuSection section =
                new MenuSection("Starters");

        Recipe recipe =
                new Recipe("Garlic Bread");

        recipe.setActive(true);

        MenuItemRequest request =
                new MenuItemRequest(
                        recipeId,
                        subsectionId,
                        0,
                        true,
                        List.of(
                                new MenuItemPriceRequest(
                                        null,
                                        new BigDecimal("6.00"),
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
        ).thenReturn(false);

        when(
                menuSubsectionRepository
                        .findByIdAndMenuSectionId(
                                subsectionId,
                                sectionId
                        )
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
                "Menu subsection does not belong to that menu section.",
                exception.getMessage()
        );

        verify(
                menuItemRepository,
                never()
        ).save(any());
    }

    @Test
    void createMenuItem_allowsNoItemPriceWhenSubsectionHasSharedPrice() {

        Long sectionId = 2L;
        Long subsectionId = 5L;
        Long recipeId = 10L;

        MenuSection section =
                new MenuSection("Beverages");

        MenuSubsection subsection =
                new MenuSubsection(
                        section,
                        "Pop"
                );

        subsection.setPrice(
                new BigDecimal("3.00")
        );

        Recipe recipe =
                new Recipe("Coke");

        recipe.setActive(true);

        MenuItemRequest request =
                new MenuItemRequest(
                        recipeId,
                        subsectionId,
                        0,
                        true,
                        List.of()
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
                menuSubsectionRepository
                        .findByIdAndMenuSectionId(
                                subsectionId,
                                sectionId
                        )
        ).thenReturn(
                Optional.of(subsection)
        );

        when(
                menuItemRepository
                        .findByMenuSectionIdOrderByDisplayOrderAsc(
                                sectionId
                        )
        ).thenReturn(
                List.of()
        );

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
                subsection,
                result.getMenuSubsection()
        );

        assertTrue(
                result.getPrices().isEmpty()
        );

        assertEquals(
                new BigDecimal("3.00"),
                result.getMenuSubsection().getPrice()
        );
    }

    @Test
    void createMenuItem_requiresItemPriceWhenSubsectionHasNoSharedPrice() {

        Long sectionId = 2L;
        Long subsectionId = 5L;
        Long recipeId = 10L;

        MenuSection section =
                new MenuSection("Beverages");

        MenuSubsection subsection =
                new MenuSubsection(
                        section,
                        "Beer"
                );

        Recipe recipe =
                new Recipe("Bud Light");

        recipe.setActive(true);

        MenuItemRequest request =
                new MenuItemRequest(
                        recipeId,
                        subsectionId,
                        0,
                        true,
                        List.of()
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
                menuSubsectionRepository
                        .findByIdAndMenuSectionId(
                                subsectionId,
                                sectionId
                        )
        ).thenReturn(
                Optional.of(subsection)
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
                "At least one price is required unless the menu subsection has a shared price.",
                exception.getMessage()
        );

        verify(
                menuItemRepository,
                never()
        ).save(any());
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

        MenuSectionNotFoundException exception =
                assertThrows(
                        MenuSectionNotFoundException.class,
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

        RecipeNotFoundException exception =
                assertThrows(
                        RecipeNotFoundException.class,
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

        DuplicateMenuItemException exception =
                assertThrows(
                        DuplicateMenuItemException.class,
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
    void updateMenuItem_throwsWhenRecipeAlreadyExistsInSection() {

        Long sectionId = 2L;
        Long menuItemId = 20L;
        Long oldRecipeId = 10L;
        Long newRecipeId = 11L;

        MenuSection section =
                new MenuSection("Entrees");

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

        MenuItemRequest request =
                new MenuItemRequest(
                        newRecipeId,
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
                recipeRepository.findById(newRecipeId)
        ).thenReturn(
                Optional.of(newRecipe)
        );

        when(
                menuItemRepository
                        .existsByMenuSectionIdAndRecipeIdAndIdNot(
                                sectionId,
                                newRecipeId,
                                menuItemId
                        )
        ).thenReturn(true);

        DuplicateMenuItemException exception =
                assertThrows(
                        DuplicateMenuItemException.class,
                        () ->
                                menuItemService.updateMenuItem(
                                        sectionId,
                                        menuItemId,
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

        MenuItemNotFoundException exception =
                assertThrows(
                        MenuItemNotFoundException.class,
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

    @Test
    void moveMenuItem_movesItemUp() {
        MenuItem first = mock(MenuItem.class);
        MenuItem second = mock(MenuItem.class);

        when(first.getId()).thenReturn(1L);
        when(first.getDisplayOrder()).thenReturn(0);

        when(second.getId()).thenReturn(2L);
        when(second.getDisplayOrder()).thenReturn(1);

        when(
                menuItemRepository
                        .findByMenuSectionIdOrderByDisplayOrderAsc(10L)
        )
                .thenReturn(List.of(first, second))
                .thenReturn(List.of(second, first));

        List<MenuItem> result =
                menuItemService.moveMenuItem(
                        10L,
                        2L,
                        MenuItemMoveDirection.UP
                );

        verify(first).setDisplayOrder(1);
        verify(second).setDisplayOrder(0);

        verify(menuItemRepository).save(first);
        verify(menuItemRepository).save(second);

        assertEquals(second, result.get(0));
        assertEquals(first, result.get(1));
    }

    @Test
    void moveMenuItem_movesItemDown() {
        MenuItem first = mock(MenuItem.class);
        MenuItem second = mock(MenuItem.class);

        when(first.getId()).thenReturn(1L);
        when(first.getDisplayOrder()).thenReturn(0);

        when(second.getDisplayOrder()).thenReturn(1);

        when(
                menuItemRepository
                        .findByMenuSectionIdOrderByDisplayOrderAsc(10L)
        )
                .thenReturn(List.of(first, second))
                .thenReturn(List.of(second, first));

        List<MenuItem> result =
                menuItemService.moveMenuItem(
                        10L,
                        1L,
                        MenuItemMoveDirection.DOWN
                );

        verify(first).setDisplayOrder(1);
        verify(second).setDisplayOrder(0);

        verify(menuItemRepository).save(first);
        verify(menuItemRepository).save(second);

        assertEquals(second, result.get(0));
        assertEquals(first, result.get(1));
    }

    @Test
    void moveMenuItem_movesOnlyWithinSameSubsection() {
        MenuSubsection pop =
                mock(MenuSubsection.class);

        MenuSubsection beer =
                mock(MenuSubsection.class);

        when(pop.getId()).thenReturn(100L);
        when(beer.getId()).thenReturn(200L);

        MenuItem coke =
                mock(MenuItem.class);

        MenuItem budLight =
                mock(MenuItem.class);

        MenuItem dietCoke =
                mock(MenuItem.class);

        MenuItem millerLite =
                mock(MenuItem.class);

        when(coke.getId()).thenReturn(1L);
        when(coke.getMenuSubsection()).thenReturn(pop);
        when(coke.getDisplayOrder()).thenReturn(0);

        when(budLight.getMenuSubsection()).thenReturn(beer);

        when(dietCoke.getMenuSubsection()).thenReturn(pop);
        when(dietCoke.getDisplayOrder()).thenReturn(2);

        when(millerLite.getMenuSubsection()).thenReturn(beer);

        when(
                menuItemRepository
                        .findByMenuSectionIdOrderByDisplayOrderAsc(10L)
        )
                .thenReturn(
                        List.of(
                                coke,
                                budLight,
                                dietCoke,
                                millerLite
                        )
                )
                .thenReturn(
                        List.of(
                                dietCoke,
                                budLight,
                                coke,
                                millerLite
                        )
                );

        List<MenuItem> result =
                menuItemService.moveMenuItem(
                        10L,
                        1L,
                        MenuItemMoveDirection.DOWN
                );

        verify(coke).setDisplayOrder(2);
        verify(dietCoke).setDisplayOrder(0);

        verify(menuItemRepository).save(coke);
        verify(menuItemRepository).save(dietCoke);

        verify(menuItemRepository, never()).save(budLight);
        verify(menuItemRepository, never()).save(millerLite);

        assertEquals(dietCoke, result.get(0));
        assertEquals(coke, result.get(2));
    }

    @Test
    void moveMenuItem_movesOnlyWithinUngroupedItems() {
        MenuSubsection subsection =
                mock(MenuSubsection.class);

        when(subsection.getId()).thenReturn(100L);

        MenuItem firstUngrouped =
                mock(MenuItem.class);

        MenuItem groupedItem =
                mock(MenuItem.class);

        MenuItem secondUngrouped =
                mock(MenuItem.class);

        when(firstUngrouped.getId()).thenReturn(1L);
        when(firstUngrouped.getDisplayOrder()).thenReturn(0);

        when(groupedItem.getMenuSubsection())
                .thenReturn(subsection);

        when(secondUngrouped.getDisplayOrder())
                .thenReturn(2);

        when(
                menuItemRepository
                        .findByMenuSectionIdOrderByDisplayOrderAsc(10L)
        )
                .thenReturn(
                        List.of(
                                firstUngrouped,
                                groupedItem,
                                secondUngrouped
                        )
                )
                .thenReturn(
                        List.of(
                                secondUngrouped,
                                groupedItem,
                                firstUngrouped
                        )
                );

        List<MenuItem> result =
                menuItemService.moveMenuItem(
                        10L,
                        1L,
                        MenuItemMoveDirection.DOWN
                );

        verify(firstUngrouped).setDisplayOrder(2);
        verify(secondUngrouped).setDisplayOrder(0);

        verify(menuItemRepository).save(firstUngrouped);
        verify(menuItemRepository).save(secondUngrouped);

        verify(menuItemRepository, never())
                .save(groupedItem);

        assertEquals(secondUngrouped, result.get(0));
        assertEquals(groupedItem, result.get(1));
        assertEquals(firstUngrouped, result.get(2));
    }

    @Test
    void moveMenuItem_doesNothingWhenAlreadyAtBoundary() {
        MenuItem first = mock(MenuItem.class);
        MenuItem second = mock(MenuItem.class);

        when(first.getId()).thenReturn(1L);

        when(
                menuItemRepository
                        .findByMenuSectionIdOrderByDisplayOrderAsc(10L)
        )
                .thenReturn(
                        List.of(first, second)
                );

        List<MenuItem> result =
                menuItemService.moveMenuItem(
                        10L,
                        1L,
                        MenuItemMoveDirection.UP
                );

        assertEquals(List.of(first, second), result);

        verify(
                menuItemRepository,
                never()
        ).save(any(MenuItem.class));

        verify(first, never())
                .setDisplayOrder(anyInt());

        verify(second, never())
                .setDisplayOrder(anyInt());
    }

    @Test
    void moveMenuItem_throwsWhenItemIsNotInSection() {
        MenuItem existing = mock(MenuItem.class);

        when(existing.getId()).thenReturn(1L);

        when(
                menuItemRepository
                        .findByMenuSectionIdOrderByDisplayOrderAsc(10L)
        )
                .thenReturn(
                        List.of(existing)
                );

        assertThrows(
                MenuItemNotFoundException.class,
                () ->
                        menuItemService.moveMenuItem(
                                10L,
                                999L,
                                MenuItemMoveDirection.DOWN
                        )
        );

        verify(
                menuItemRepository,
                never()
        ).save(any(MenuItem.class));
    }

}