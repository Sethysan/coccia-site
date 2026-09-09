package com.cocciahouse.api.service;

import com.cocciahouse.api.dto.menu.PizzaSpecialtyPriceRequest;
import com.cocciahouse.api.dto.menu.PizzaSpecialtyRequest;
import com.cocciahouse.api.dto.menu.PizzaSpecialtyAddOnRequest;
import com.cocciahouse.api.model.PizzaSpecialtyAddOnPricingType;
import com.cocciahouse.api.model.MenuSection;
import com.cocciahouse.api.model.PizzaSize;
import com.cocciahouse.api.model.PizzaSpecialty;
import com.cocciahouse.api.model.Recipe;
import com.cocciahouse.api.model.PizzaSpecialtyPricingMode;
import com.cocciahouse.api.model.PizzaAddOn;
import com.cocciahouse.api.model.PizzaTopping;
import com.cocciahouse.api.repository.MenuSectionRepository;
import com.cocciahouse.api.repository.PizzaSizeRepository;
import com.cocciahouse.api.repository.PizzaSpecialtyRepository;
import com.cocciahouse.api.repository.RecipeRepository;
import com.cocciahouse.api.repository.PizzaAddOnRepository;
import com.cocciahouse.api.repository.PizzaToppingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PizzaSpecialtyServiceTest {

    private PizzaSpecialtyRepository pizzaSpecialtyRepository;
    private MenuSectionRepository menuSectionRepository;
    private RecipeRepository recipeRepository;
    private PizzaSizeRepository pizzaSizeRepository;
    private PizzaToppingRepository pizzaToppingRepository;
    private PizzaAddOnRepository pizzaAddOnRepository;

    private PizzaSpecialtyService pizzaSpecialtyService;

    @BeforeEach
    void setUp() {
        pizzaSpecialtyRepository =
                mock(PizzaSpecialtyRepository.class);

        menuSectionRepository =
                mock(MenuSectionRepository.class);

        recipeRepository =
                mock(RecipeRepository.class);

        pizzaSizeRepository =
                mock(PizzaSizeRepository.class);

        pizzaToppingRepository =
                mock(PizzaToppingRepository.class);

        pizzaAddOnRepository =
                mock(PizzaAddOnRepository.class);

        pizzaSpecialtyService =
                new PizzaSpecialtyService(
                        pizzaSpecialtyRepository,
                        menuSectionRepository,
                        recipeRepository,
                        pizzaSizeRepository,
                        pizzaToppingRepository,
                        pizzaAddOnRepository
                );
    }

    @Test
    void createSpecialty_appendsAndCreatesPrices() {
        MenuSection section =
                new MenuSection("Pizza");

        Recipe recipe =
                createActiveRecipe();

        PizzaSpecialty existing =
                new PizzaSpecialty();

        existing.setDisplayOrder(2);

        PizzaSize small =
                createSize(section, "Small", 0);

        PizzaSize medium =
                createSize(section, "Medium", 1);

        when(menuSectionRepository.findById(1L))
                .thenReturn(Optional.of(section));

        when(recipeRepository.findById(10L))
                .thenReturn(Optional.of(recipe));

        when(
                pizzaSpecialtyRepository
                        .existsByMenuSectionIdAndRecipeId(
                                1L,
                                10L
                        )
        ).thenReturn(false);

        when(
                pizzaSpecialtyRepository
                        .findByMenuSectionIdWithDetails(1L)
        ).thenReturn(List.of(existing));

        when(
                pizzaSizeRepository
                        .findByIdAndMenuSectionId(100L, 1L)
        ).thenReturn(Optional.of(small));

        when(
                pizzaSizeRepository
                        .findByIdAndMenuSectionId(101L, 1L)
        ).thenReturn(Optional.of(medium));

        when(
                pizzaSpecialtyRepository
                        .save(any(PizzaSpecialty.class))
        ).thenAnswer(
                invocation ->
                        invocation.getArgument(0)
        );

        PizzaSpecialtyRequest request =
                new PizzaSpecialtyRequest(
                        10L,
                        true,
                        PizzaSpecialtyPricingMode.CUSTOM,
                        List.of(),
                        List.of(),
                        List.of(
                                new PizzaSpecialtyPriceRequest(
                                        100L,
                                        new BigDecimal("29.00")
                                ),
                                new PizzaSpecialtyPriceRequest(
                                        101L,
                                        new BigDecimal("32.00")
                                )
                        )
                );

        pizzaSpecialtyService.createSpecialty(
                1L,
                request
        );

        ArgumentCaptor<PizzaSpecialty> captor =
                ArgumentCaptor.forClass(
                        PizzaSpecialty.class
                );

        verify(pizzaSpecialtyRepository)
                .save(captor.capture());

        PizzaSpecialty saved =
                captor.getValue();

        assertSame(recipe, saved.getRecipe());
        assertSame(section, saved.getMenuSection());
        assertEquals(3, saved.getDisplayOrder());
        assertTrue(saved.isActive());

        assertEquals(
                2,
                saved.getPrices().size()
        );

        assertEquals(
                new BigDecimal("29.00"),
                saved.getPrices().getFirst().getAmount()
        );

        assertSame(
                small,
                saved.getPrices().getFirst().getPizzaSize()
        );

        assertSame(
                saved,
                saved.getPrices()
                        .getFirst()
                        .getPizzaSpecialty()
        );
    }

    @Test
    void createSpecialty_rejectsDuplicateSizeIds() {
        MenuSection section =
                new MenuSection("Pizza");

        Recipe recipe =
                createActiveRecipe();

        PizzaSize small =
                createSize(section, "Small", 0);

        when(menuSectionRepository.findById(1L))
                .thenReturn(Optional.of(section));

        when(recipeRepository.findById(10L))
                .thenReturn(Optional.of(recipe));

        when(
                pizzaSpecialtyRepository
                        .existsByMenuSectionIdAndRecipeId(
                                1L,
                                10L
                        )
        ).thenReturn(false);

        when(
                pizzaSpecialtyRepository
                        .findByMenuSectionIdWithDetails(1L)
        ).thenReturn(List.of());

        when(
                pizzaSizeRepository
                        .findByIdAndMenuSectionId(100L, 1L)
        ).thenReturn(Optional.of(small));

        PizzaSpecialtyRequest request =
                new PizzaSpecialtyRequest(
                        10L,
                        true,
                        PizzaSpecialtyPricingMode.CUSTOM,
                        List.of(),
                        List.of(),
                        List.of(
                                new PizzaSpecialtyPriceRequest(
                                        100L,
                                        new BigDecimal("29.00")
                                ),
                                new PizzaSpecialtyPriceRequest(
                                        100L,
                                        new BigDecimal("30.00")
                                )
                        )
                );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                pizzaSpecialtyService
                                        .createSpecialty(
                                                1L,
                                                request
                                        )
                );

        assertEquals(
                "Pizza size appears more than once in specialty prices: 100",
                exception.getMessage()
        );

        verify(
                pizzaSpecialtyRepository,
                never()
        ).save(any());
    }

    @Test
    void createSpecialty_rejectsSizeFromAnotherSection() {
        MenuSection section =
                new MenuSection("Pizza");

        Recipe recipe =
                createActiveRecipe();

        when(menuSectionRepository.findById(1L))
                .thenReturn(Optional.of(section));

        when(recipeRepository.findById(10L))
                .thenReturn(Optional.of(recipe));

        when(
                pizzaSpecialtyRepository
                        .existsByMenuSectionIdAndRecipeId(
                                1L,
                                10L
                        )
        ).thenReturn(false);

        when(
                pizzaSpecialtyRepository
                        .findByMenuSectionIdWithDetails(1L)
        ).thenReturn(List.of());

        /*
         * Size 999 may exist somewhere in the database,
         * but it does NOT belong to section 1.
         */
        when(
                pizzaSizeRepository
                        .findByIdAndMenuSectionId(999L, 1L)
        ).thenReturn(Optional.empty());

        PizzaSpecialtyRequest request =
                new PizzaSpecialtyRequest(
                        10L,
                        true,
                        PizzaSpecialtyPricingMode.CUSTOM,
                        List.of(),
                        List.of(),
                        List.of(
                                new PizzaSpecialtyPriceRequest(
                                        999L,
                                        new BigDecimal("29.00")
                                )
                        )
                );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                pizzaSpecialtyService
                                        .createSpecialty(
                                                1L,
                                                request
                                        )
                );

        assertEquals(
                "Pizza size 999 does not belong to menu section 1",
                exception.getMessage()
        );

        verify(
                pizzaSpecialtyRepository,
                never()
        ).save(any());
    }

    @Test
    void createSpecialty_rejectsInactiveRecipe() {
        MenuSection section =
                new MenuSection("Pizza");

        Recipe recipe =
                new Recipe();

        recipe.setName("Old Specialty");
        recipe.setActive(false);

        when(menuSectionRepository.findById(1L))
                .thenReturn(Optional.of(section));

        when(recipeRepository.findById(10L))
                .thenReturn(Optional.of(recipe));

        PizzaSpecialtyRequest request =
                new PizzaSpecialtyRequest(
                        10L,
                        true,
                        PizzaSpecialtyPricingMode.CUSTOM,
                        List.of(),
                        List.of(),
                        List.of(
                                new PizzaSpecialtyPriceRequest(
                                        100L,
                                        new BigDecimal("29.00")
                                )
                        )
                );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                pizzaSpecialtyService
                                        .createSpecialty(
                                                1L,
                                                request
                                        )
                );

        assertEquals(
                "Inactive recipe cannot be added as a specialty pizza: Old Specialty",
                exception.getMessage()
        );

        verify(
                pizzaSpecialtyRepository,
                never()
        ).save(any());
    }

    @Test
    void createCalculatedSpecialty_acceptsToppingsWithoutCustomPrices() {
        MenuSection section =
                new MenuSection("Pizza");

        Recipe recipe =
                createActiveRecipe();

        PizzaTopping pepperoni =
                new PizzaTopping();

        pepperoni.setMenuSection(section);
        pepperoni.setName("Pepperoni");
        pepperoni.setActive(true);

        when(menuSectionRepository.findById(1L))
                .thenReturn(Optional.of(section));

        when(recipeRepository.findById(10L))
                .thenReturn(Optional.of(recipe));

        when(
                pizzaSpecialtyRepository
                        .existsByMenuSectionIdAndRecipeId(
                                1L,
                                10L
                        )
        ).thenReturn(false);

        when(
                pizzaSpecialtyRepository
                        .findByMenuSectionIdWithDetails(1L)
        ).thenReturn(List.of());

        when(
                pizzaToppingRepository
                        .findByIdAndMenuSectionId(
                                200L,
                                1L
                        )
        ).thenReturn(Optional.of(pepperoni));

        when(
                pizzaSpecialtyRepository
                        .save(any(PizzaSpecialty.class))
        ).thenAnswer(
                invocation ->
                        invocation.getArgument(0)
        );

        PizzaSpecialtyRequest request =
                new PizzaSpecialtyRequest(
                        10L,
                        true,
                        PizzaSpecialtyPricingMode.CALCULATED,
                        List.of(200L),
                        List.of(),
                        List.of()
                );

        pizzaSpecialtyService.createSpecialty(
                1L,
                request
        );

        ArgumentCaptor<PizzaSpecialty> captor =
                ArgumentCaptor.forClass(
                        PizzaSpecialty.class
                );

        verify(pizzaSpecialtyRepository)
                .save(captor.capture());

        PizzaSpecialty saved =
                captor.getValue();

        assertEquals(
                PizzaSpecialtyPricingMode.CALCULATED,
                saved.getPricingMode()
        );

        assertEquals(
                1,
                saved.getToppings().size()
        );

        assertSame(
                pepperoni,
                saved.getToppings().getFirst()
        );

        assertTrue(saved.getPrices().isEmpty());
    }

    @Test
    void createCalculatedSpecialty_storesIncludedAddOns() {
        MenuSection section =
                new MenuSection("Pizza");

        Recipe recipe =
                createActiveRecipe();

        PizzaAddOn doubleCheese =
                new PizzaAddOn();

        doubleCheese.setMenuSection(section);
        doubleCheese.setName("Double Cheese");
        doubleCheese.setActive(true);

        when(menuSectionRepository.findById(1L))
                .thenReturn(Optional.of(section));

        when(recipeRepository.findById(10L))
                .thenReturn(Optional.of(recipe));

        when(
                pizzaSpecialtyRepository
                        .existsByMenuSectionIdAndRecipeId(
                                1L,
                                10L
                        )
        ).thenReturn(false);

        when(
                pizzaSpecialtyRepository
                        .findByMenuSectionIdWithDetails(1L)
        ).thenReturn(List.of());

        when(
                pizzaAddOnRepository
                        .findByIdAndMenuSectionId(
                                300L,
                                1L
                        )
        ).thenReturn(Optional.of(doubleCheese));

        when(
                pizzaSpecialtyRepository
                        .save(any(PizzaSpecialty.class))
        ).thenAnswer(
                invocation ->
                        invocation.getArgument(0)
        );

        PizzaSpecialtyRequest request =
                new PizzaSpecialtyRequest(
                        10L,
                        true,
                        PizzaSpecialtyPricingMode.CALCULATED,
                        List.of(),
                        List.of(
                                new PizzaSpecialtyAddOnRequest(
                                        300L,
                                        PizzaSpecialtyAddOnPricingType.FREE,
                                        null
                                )
                        ),
                        List.of()
                );

        pizzaSpecialtyService.createSpecialty(
                1L,
                request
        );

        ArgumentCaptor<PizzaSpecialty> captor =
                ArgumentCaptor.forClass(
                        PizzaSpecialty.class
                );

        verify(pizzaSpecialtyRepository)
                .save(captor.capture());

        PizzaSpecialty saved =
                captor.getValue();

        assertEquals(
                1,
                saved.getSpecialtyAddOns().size()
        );

        assertSame(
                doubleCheese,
                saved.getSpecialtyAddOns()
                        .getFirst()
                        .getPizzaAddOn()
        );

        assertEquals(
                PizzaSpecialtyAddOnPricingType.FREE,
                saved.getSpecialtyAddOns()
                        .getFirst()
                        .getPricingType()
        );
    }

    @Test
    void createCalculatedSpecialty_acceptsCustomAddOnPrice() {
        MenuSection section =
                new MenuSection("Pizza");

        Recipe recipe =
                createActiveRecipe();

        PizzaAddOn doubleCheese =
                new PizzaAddOn();

        doubleCheese.setMenuSection(section);
        doubleCheese.setName("Double Cheese");
        doubleCheese.setActive(true);

        when(menuSectionRepository.findById(1L))
                .thenReturn(Optional.of(section));

        when(recipeRepository.findById(10L))
                .thenReturn(Optional.of(recipe));

        when(
                pizzaSpecialtyRepository
                        .existsByMenuSectionIdAndRecipeId(
                                1L,
                                10L
                        )
        ).thenReturn(false);

        when(
                pizzaSpecialtyRepository
                        .findByMenuSectionIdWithDetails(1L)
        ).thenReturn(List.of());

        when(
                pizzaAddOnRepository
                        .findByIdAndMenuSectionId(
                                300L,
                                1L
                        )
        ).thenReturn(Optional.of(doubleCheese));

        when(
                pizzaSpecialtyRepository
                        .save(any(PizzaSpecialty.class))
        ).thenAnswer(
                invocation ->
                        invocation.getArgument(0)
        );

        PizzaSpecialtyRequest request =
                new PizzaSpecialtyRequest(
                        10L,
                        true,
                        PizzaSpecialtyPricingMode.CALCULATED,
                        List.of(),
                        List.of(
                                new PizzaSpecialtyAddOnRequest(
                                        300L,
                                        PizzaSpecialtyAddOnPricingType.CUSTOM,
                                        new BigDecimal("1.00")
                                )
                        ),
                        List.of()
                );

        pizzaSpecialtyService.createSpecialty(
                1L,
                request
        );

        ArgumentCaptor<PizzaSpecialty> captor =
                ArgumentCaptor.forClass(
                        PizzaSpecialty.class
                );

        verify(pizzaSpecialtyRepository)
                .save(captor.capture());

        PizzaSpecialty saved =
                captor.getValue();

        assertEquals(
                PizzaSpecialtyAddOnPricingType.CUSTOM,
                saved.getSpecialtyAddOns()
                        .getFirst()
                        .getPricingType()
        );

        assertEquals(
                new BigDecimal("1.00"),
                saved.getSpecialtyAddOns()
                        .getFirst()
                        .getOverrideAmount()
        );
    }

    @Test
    void createCalculatedSpecialty_rejectsCustomAddOnWithoutPrice() {
        MenuSection section =
                new MenuSection("Pizza");

        Recipe recipe =
                createActiveRecipe();

        PizzaAddOn doubleCheese =
                new PizzaAddOn();

        doubleCheese.setMenuSection(section);
        doubleCheese.setName("Double Cheese");
        doubleCheese.setActive(true);

        when(menuSectionRepository.findById(1L))
                .thenReturn(Optional.of(section));

        when(recipeRepository.findById(10L))
                .thenReturn(Optional.of(recipe));

        when(
                pizzaSpecialtyRepository
                        .existsByMenuSectionIdAndRecipeId(
                                1L,
                                10L
                        )
        ).thenReturn(false);

        when(
                pizzaSpecialtyRepository
                        .findByMenuSectionIdWithDetails(1L)
        ).thenReturn(List.of());

        when(
                pizzaAddOnRepository
                        .findByIdAndMenuSectionId(
                                300L,
                                1L
                        )
        ).thenReturn(Optional.of(doubleCheese));

        PizzaSpecialtyRequest request =
                new PizzaSpecialtyRequest(
                        10L,
                        true,
                        PizzaSpecialtyPricingMode.CALCULATED,
                        List.of(),
                        List.of(
                                new PizzaSpecialtyAddOnRequest(
                                        300L,
                                        PizzaSpecialtyAddOnPricingType.CUSTOM,
                                        null
                                )
                        ),
                        List.of()
                );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                pizzaSpecialtyService
                                        .createSpecialty(
                                                1L,
                                                request
                                        )
                );

        assertEquals(
                "Custom specialty add-on pricing requires an amount",
                exception.getMessage()
        );

        verify(
                pizzaSpecialtyRepository,
                never()
        ).save(any());
    }

    @Test
    void createCalculatedSpecialty_rejectsInvalidAddOnPricingCombinations() {
        MenuSection section =
                new MenuSection("Pizza");

        Recipe recipe =
                createActiveRecipe();

        PizzaAddOn doubleCheese =
                new PizzaAddOn();

        doubleCheese.setMenuSection(section);
        doubleCheese.setName("Double Cheese");
        doubleCheese.setActive(true);

        when(menuSectionRepository.findById(1L))
                .thenReturn(Optional.of(section));

        when(recipeRepository.findById(10L))
                .thenReturn(Optional.of(recipe));

        when(
                pizzaSpecialtyRepository
                        .existsByMenuSectionIdAndRecipeId(
                                1L,
                                10L
                        )
        ).thenReturn(false);

        when(
                pizzaSpecialtyRepository
                        .findByMenuSectionIdWithDetails(1L)
        ).thenReturn(List.of());

        when(
                pizzaAddOnRepository
                        .findByIdAndMenuSectionId(
                                300L,
                                1L
                        )
        ).thenReturn(Optional.of(doubleCheese));

        PizzaSpecialtyRequest standardWithOverride =
                new PizzaSpecialtyRequest(
                        10L,
                        true,
                        PizzaSpecialtyPricingMode.CALCULATED,
                        List.of(),
                        List.of(
                                new PizzaSpecialtyAddOnRequest(
                                        300L,
                                        PizzaSpecialtyAddOnPricingType.STANDARD,
                                        new BigDecimal("1.00")
                                )
                        ),
                        List.of()
                );

        IllegalArgumentException standardException =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> pizzaSpecialtyService.createSpecialty(
                                1L,
                                standardWithOverride
                        )
                );

        assertEquals(
                "Only custom specialty add-on pricing can have an override amount",
                standardException.getMessage()
        );

        PizzaSpecialtyRequest freeWithOverride =
                new PizzaSpecialtyRequest(
                        10L,
                        true,
                        PizzaSpecialtyPricingMode.CALCULATED,
                        List.of(),
                        List.of(
                                new PizzaSpecialtyAddOnRequest(
                                        300L,
                                        PizzaSpecialtyAddOnPricingType.FREE,
                                        new BigDecimal("1.00")
                                )
                        ),
                        List.of()
                );

        IllegalArgumentException freeException =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> pizzaSpecialtyService.createSpecialty(
                                1L,
                                freeWithOverride
                        )
                );

        assertEquals(
                "Only custom specialty add-on pricing can have an override amount",
                freeException.getMessage()
        );

        PizzaSpecialtyRequest negativeCustom =
                new PizzaSpecialtyRequest(
                        10L,
                        true,
                        PizzaSpecialtyPricingMode.CALCULATED,
                        List.of(),
                        List.of(
                                new PizzaSpecialtyAddOnRequest(
                                        300L,
                                        PizzaSpecialtyAddOnPricingType.CUSTOM,
                                        new BigDecimal("-1.00")
                                )
                        ),
                        List.of()
                );

        IllegalArgumentException negativeException =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> pizzaSpecialtyService.createSpecialty(
                                1L,
                                negativeCustom
                        )
                );

        assertEquals(
                "Specialty add-on price cannot be negative",
                negativeException.getMessage()
        );

        verify(
                pizzaSpecialtyRepository,
                never()
        ).save(any());
    }

    @Test
    void createCustomSpecialty_rejectsMissingPrices() {
        MenuSection section =
                new MenuSection("Pizza");

        Recipe recipe =
                createActiveRecipe();

        when(menuSectionRepository.findById(1L))
                .thenReturn(Optional.of(section));

        when(recipeRepository.findById(10L))
                .thenReturn(Optional.of(recipe));

        when(
                pizzaSpecialtyRepository
                        .existsByMenuSectionIdAndRecipeId(
                                1L,
                                10L
                        )
        ).thenReturn(false);

        when(
                pizzaSpecialtyRepository
                        .findByMenuSectionIdWithDetails(1L)
        ).thenReturn(List.of());

        PizzaSpecialtyRequest request =
                new PizzaSpecialtyRequest(
                        10L,
                        true,
                        PizzaSpecialtyPricingMode.CUSTOM,
                        List.of(),
                        List.of(),
                        List.of()
                );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                pizzaSpecialtyService
                                        .createSpecialty(
                                                1L,
                                                request
                                        )
                );

        assertEquals(
                "Custom specialty pizzas require at least one price",
                exception.getMessage()
        );

        verify(
                pizzaSpecialtyRepository,
                never()
        ).save(any());
    }

    private Recipe createActiveRecipe(
    ) {
        Recipe recipe =
                new Recipe();

        recipe.setName("Works");
        recipe.setActive(true);

        return recipe;
    }

    private PizzaSize createSize(
            MenuSection section,
            String name,
            int displayOrder
    ) {
        PizzaSize pizzaSize =
                new PizzaSize();

        pizzaSize.setMenuSection(section);
        pizzaSize.setName(name);
        pizzaSize.setDisplayOrder(displayOrder);
        pizzaSize.setActive(true);

        return pizzaSize;
    }

    @Test
    void updateSpecialty_allowsPriceEditWhenExistingRecipeIsInactive() {
        MenuSection section =
                new MenuSection("Pizza");

        Recipe recipe =
                mock(Recipe.class);

        when(recipe.getId())
                .thenReturn(10L);

        when(recipe.getName())
                .thenReturn("Works");

        when(recipe.isActive())
                .thenReturn(false);

        PizzaSize small =
                createSize(section, "Small", 0);

        PizzaSpecialty specialty =
                new PizzaSpecialty();

        specialty.setMenuSection(section);
        specialty.setRecipe(recipe);
        specialty.setDisplayOrder(0);
        specialty.setActive(true);

        when(
                menuSectionRepository.existsById(1L)
        ).thenReturn(true);

        when(
                pizzaSpecialtyRepository
                        .findByIdAndMenuSectionIdWithDetails(
                                20L,
                                1L
                        )
        ).thenReturn(
                Optional.of(specialty)
        );

        when(
                recipeRepository.findById(10L)
        ).thenReturn(
                Optional.of(recipe)
        );

        when(
                pizzaSpecialtyRepository
                        .existsByMenuSectionIdAndRecipeIdAndIdNot(
                                1L,
                                10L,
                                20L
                        )
        ).thenReturn(false);

        when(
                pizzaSizeRepository
                        .findByIdAndMenuSectionId(
                                100L,
                                1L
                        )
        ).thenReturn(
                Optional.of(small)
        );

        when(
                pizzaSpecialtyRepository.save(specialty)
        ).thenReturn(specialty);

        PizzaSpecialtyRequest request =
                new PizzaSpecialtyRequest(
                        10L,
                        true,
                        PizzaSpecialtyPricingMode.CUSTOM,
                        List.of(),
                        List.of(),
                        List.of(
                                new PizzaSpecialtyPriceRequest(
                                        100L,
                                        new BigDecimal("31.00")
                                )
                        )
                );

        assertDoesNotThrow(
                () ->
                        pizzaSpecialtyService.updateSpecialty(
                                1L,
                                20L,
                                request
                        )
        );

        assertEquals(
                1,
                specialty.getPrices().size()
        );

        assertEquals(
                new BigDecimal("31.00"),
                specialty.getPrices()
                        .getFirst()
                        .getAmount()
        );
    }

}