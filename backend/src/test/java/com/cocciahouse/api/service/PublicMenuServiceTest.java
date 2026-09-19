package com.cocciahouse.api.service;

import com.cocciahouse.api.dto.menu.PublicMenuResponse;
import com.cocciahouse.api.model.MenuSection;
import com.cocciahouse.api.model.MenuSubsection;
import com.cocciahouse.api.model.MenuItem;
import com.cocciahouse.api.model.MenuItemPrice;
import com.cocciahouse.api.model.PizzaSpecialty;
import com.cocciahouse.api.model.PizzaSpecialtyPrice;
import com.cocciahouse.api.model.PizzaSpecialtyPricingMode;
import com.cocciahouse.api.model.Recipe;
import com.cocciahouse.api.model.PizzaSpecialtyAddOn;
import com.cocciahouse.api.model.PizzaSpecialtyAddOnPricingType;
import com.cocciahouse.api.model.PizzaSize;
import com.cocciahouse.api.model.PizzaTopping;
import com.cocciahouse.api.model.PizzaAddOn;
import com.cocciahouse.api.model.PizzaAddOnType;

import java.math.BigDecimal;

import com.cocciahouse.api.repository.MenuItemRepository;
import com.cocciahouse.api.repository.MenuSectionRepository;
import com.cocciahouse.api.repository.MenuSubsectionRepository;
import com.cocciahouse.api.repository.PizzaSizeRepository;
import com.cocciahouse.api.repository.PizzaToppingRepository;
import com.cocciahouse.api.repository.PizzaAddOnRepository;
import com.cocciahouse.api.repository.PizzaSpecialtyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PublicMenuServiceTest {

    @Mock
    private MenuSectionRepository menuSectionRepository;

    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private MenuSubsectionRepository menuSubsectionRepository;

    @Mock
    private PizzaSizeRepository pizzaSizeRepository;

    @Mock
    private PizzaToppingRepository pizzaToppingRepository;

    @Mock
    private PizzaAddOnRepository pizzaAddOnRepository;

    @Mock
    private PizzaSpecialtyRepository pizzaSpecialtyRepository;

    private PublicMenuService publicMenuService;

    @BeforeEach
    void setUp() {
        publicMenuService =
                new PublicMenuService(
                        menuSectionRepository,
                        menuItemRepository,
                        menuSubsectionRepository,
                        pizzaSizeRepository,
                        pizzaToppingRepository,
                        pizzaAddOnRepository,
                        pizzaSpecialtyRepository
                );
    }

    @Test
    void getMenu_returnsActiveSectionsInRepositoryOrder() {

        MenuSection starters =
                mock(MenuSection.class);

        when(starters.getId())
                .thenReturn(1L);

        when(starters.getName())
                .thenReturn("Starters");

        MenuSection salads =
                mock(MenuSection.class);

        when(salads.getId())
                .thenReturn(2L);

        when(salads.getName())
                .thenReturn("Salads");

        when(
                menuSectionRepository
                        .findByActiveTrueOrderByDisplayOrderAsc()
        ).thenReturn(
                List.of(
                        starters,
                        salads
                )
        );

        when(
                menuSubsectionRepository
                        .findByMenuSectionIdAndActiveTrueOrderByDisplayOrderAscIdAsc(
                                anyLong()
                        )
        ).thenReturn(List.of());

        when(
                menuItemRepository
                        .findByMenuSectionIdAndVisibleTrueOrderByDisplayOrderAsc(
                                anyLong()
                        )
        ).thenReturn(List.of());

        PublicMenuResponse result =
                publicMenuService.getMenu();

        assertEquals(
                2,
                result.sections().size()
        );

        assertEquals(
                "Starters",
                result.sections().get(0).name()
        );

        assertEquals(
                "Salads",
                result.sections().get(1).name()
        );

        verify(
                menuSectionRepository
        ).findByActiveTrueOrderByDisplayOrderAsc();
    }

    @Test
    void getMenu_mapsVisibleUngroupedItemWithRecipeAndPrice() {

        MenuSection starters =
                mock(MenuSection.class);

        when(starters.getId())
                .thenReturn(1L);

        when(starters.getName())
                .thenReturn("Starters");

        Recipe garlicBread =
                new Recipe("Garlic Bread");

        garlicBread.setDescription(
                "Homemade bread with garlic butter"
        );

        garlicBread.setImageUrl(
                "https://example.com/garlic-bread.jpg"
        );

        garlicBread.setImageAlt(
                "Garlic bread"
        );

        MenuItem menuItem =
                new MenuItem(
                        starters,
                        garlicBread
                );

        MenuItemPrice price =
                new MenuItemPrice(
                        menuItem,
                        null,
                        new BigDecimal("6.00")
                );

        menuItem.addPrice(price);

        when(
                menuSectionRepository
                        .findByActiveTrueOrderByDisplayOrderAsc()
        ).thenReturn(
                List.of(starters)
        );

        when(
                menuSubsectionRepository
                        .findByMenuSectionIdAndActiveTrueOrderByDisplayOrderAscIdAsc(
                                1L
                        )
        ).thenReturn(List.of());

        when(
                menuItemRepository
                        .findByMenuSectionIdAndVisibleTrueOrderByDisplayOrderAsc(
                                1L
                        )
        ).thenReturn(
                List.of(menuItem)
        );

        PublicMenuResponse result =
                publicMenuService.getMenu();

        assertEquals(
                1,
                result.sections().size()
        );

        assertEquals(
                1,
                result.sections().get(0).items().size()
        );

        assertEquals(
                "Garlic Bread",
                result.sections().get(0)
                        .items().get(0)
                        .name()
        );

        assertEquals(
                "Homemade bread with garlic butter",
                result.sections().get(0)
                        .items().get(0)
                        .description()
        );

        assertEquals(
                new BigDecimal("6.00"),
                result.sections().get(0)
                        .items().get(0)
                        .prices().get(0)
                        .amount()
        );
    }

    @Test
    void getMenu_groupsVisibleItemInsideActiveSubsection() {

        MenuSection beverages =
                mock(MenuSection.class);

        when(beverages.getId())
                .thenReturn(1L);

        when(beverages.getName())
                .thenReturn("Beverages");

        MenuSubsection pop =
                mock(MenuSubsection.class);

        when(pop.getId())
                .thenReturn(10L);

        when(pop.getName())
                .thenReturn("Pop");

        when(pop.getPrice())
                .thenReturn(new BigDecimal("3.00"));

        Recipe coke =
                new Recipe("Coke");

        MenuItem cokeItem =
                new MenuItem(
                        beverages,
                        coke
                );

        cokeItem.setMenuSubsection(pop);

        when(
                menuSectionRepository
                        .findByActiveTrueOrderByDisplayOrderAsc()
        ).thenReturn(
                List.of(beverages)
        );

        when(
                menuSubsectionRepository
                        .findByMenuSectionIdAndActiveTrueOrderByDisplayOrderAscIdAsc(
                                1L
                        )
        ).thenReturn(
                List.of(pop)
        );

        when(
                menuItemRepository
                        .findByMenuSectionIdAndVisibleTrueOrderByDisplayOrderAsc(
                                1L
                        )
        ).thenReturn(
                List.of(cokeItem)
        );

        PublicMenuResponse result =
                publicMenuService.getMenu();

        assertEquals(
                1,
                result.sections().get(0)
                        .subsections().size()
        );

        assertEquals(
                "Pop",
                result.sections().get(0)
                        .subsections().get(0)
                        .name()
        );

        assertEquals(
                new BigDecimal("3.00"),
                result.sections().get(0)
                        .subsections().get(0)
                        .price()
        );

        assertEquals(
                1,
                result.sections().get(0)
                        .subsections().get(0)
                        .items().size()
        );

        assertEquals(
                "Coke",
                result.sections().get(0)
                        .subsections().get(0)
                        .items().get(0)
                        .name()
        );

        assertTrue(
                result.sections().get(0)
                        .items().isEmpty()
        );
    }

    @Test
    void getMenu_requestsOnlyActiveSubsections() {

        MenuSection beverages =
                mock(MenuSection.class);

        when(beverages.getId())
                .thenReturn(1L);

        when(beverages.getName())
                .thenReturn("Beverages");

        when(
                menuSectionRepository
                        .findByActiveTrueOrderByDisplayOrderAsc()
        ).thenReturn(
                List.of(beverages)
        );

        when(
                menuSubsectionRepository
                        .findByMenuSectionIdAndActiveTrueOrderByDisplayOrderAscIdAsc(
                                1L
                        )
        ).thenReturn(List.of());

        when(
                menuItemRepository
                        .findByMenuSectionIdAndVisibleTrueOrderByDisplayOrderAsc(
                                1L
                        )
        ).thenReturn(List.of());

        PublicMenuResponse result =
                publicMenuService.getMenu();

        assertTrue(
                result.sections().get(0)
                        .subsections().isEmpty()
        );

        verify(
                menuSubsectionRepository
        ).findByMenuSectionIdAndActiveTrueOrderByDisplayOrderAscIdAsc(
                1L
        );
    }

    @Test
    void getMenu_ordersItemPricesByDisplayOrder() {

        MenuSection pasta =
                mock(MenuSection.class);

        when(pasta.getId())
                .thenReturn(1L);

        when(pasta.getName())
                .thenReturn("Pasta");

        when(menuSectionRepository
                .findByActiveTrueOrderByDisplayOrderAsc())
                .thenReturn(List.of(pasta));

        when(menuSubsectionRepository
                .findByMenuSectionIdAndActiveTrueOrderByDisplayOrderAscIdAsc(1L))
                .thenReturn(List.of());

        Recipe spaghetti =
                new Recipe("Homemade Spaghetti");

        MenuItem menuItem =
                new MenuItem(
                        pasta,
                        spaghetti
                );

        MenuItemPrice large =
                new MenuItemPrice(
                        menuItem,
                        "Large",
                        new BigDecimal("20.00")
                );

        large.setDisplayOrder(1);

        MenuItemPrice regular =
                new MenuItemPrice(
                        menuItem,
                        "Regular",
                        new BigDecimal("16.00")
                );

        regular.setDisplayOrder(0);

        /*
         * Deliberately add these in the wrong order.
         * PublicMenuService must use displayOrder,
         * not collection/insertion order.
         */
        menuItem.getPrices().add(large);
        menuItem.getPrices().add(regular);

        when(menuItemRepository
                .findByMenuSectionIdAndVisibleTrueOrderByDisplayOrderAsc(1L))
                .thenReturn(List.of(menuItem));

        var response =
                publicMenuService.getMenu();

        var prices =
                response.sections()
                        .getFirst()
                        .items()
                        .getFirst()
                        .prices();

        assertEquals(
                2,
                prices.size()
        );

        assertEquals(
                "Regular",
                prices.get(0).label()
        );

        assertEquals(
                new BigDecimal("16.00"),
                prices.get(0).amount()
        );

        assertEquals(
                "Large",
                prices.get(1).label()
        );

        assertEquals(
                new BigDecimal("20.00"),
                prices.get(1).amount()
        );
    }

    @Test
    void getMenu_mapsActivePizzaSizesIntoSectionPizzaData() {

        MenuSection pizzaSection = mock(MenuSection.class);

        when(pizzaSection.getId())
                .thenReturn(7L);

        when(pizzaSection.getName())
                .thenReturn("Pizza");

        when(menuSectionRepository
                .findByActiveTrueOrderByDisplayOrderAsc())
                .thenReturn(List.of(pizzaSection));

        when(menuItemRepository
                .findByMenuSectionIdAndVisibleTrueOrderByDisplayOrderAsc(7L))
                .thenReturn(List.of());

        when(menuSubsectionRepository
                .findByMenuSectionIdAndActiveTrueOrderByDisplayOrderAscIdAsc(7L))
                .thenReturn(List.of());

        PizzaSize small = new PizzaSize();
        small.setMenuSection(pizzaSection);
        small.setName("Small");
        small.setBasePrice(new BigDecimal("13.00"));
        small.setDisplayOrder(0);
        small.setActive(true);

        when(pizzaSizeRepository
                .findByMenuSectionIdAndActiveTrueOrderByDisplayOrderAsc(7L))
                .thenReturn(List.of(small));

        var response = publicMenuService.getMenu();

        assertEquals(
                1,
                response.sections().size()
        );

        var section = response.sections().getFirst();

        assertNotNull(section.pizza());

        assertEquals(
                1,
                section.pizza().sizes().size()
        );

        assertEquals(
                "Small",
                section.pizza().sizes().getFirst().name()
        );

        assertEquals(
                new BigDecimal("13.00"),
                section.pizza().sizes().getFirst().basePrice()
        );
    }

    @Test
    void getMenu_mapsActivePizzaToppingsIntoSectionPizzaData() {

        MenuSection pizzaSection = mock(MenuSection.class);

        when(pizzaSection.getId())
                .thenReturn(7L);

        when(pizzaSection.getName())
                .thenReturn("Pizza");

        when(menuSectionRepository
                .findByActiveTrueOrderByDisplayOrderAsc())
                .thenReturn(List.of(pizzaSection));

        when(menuItemRepository
                .findByMenuSectionIdAndVisibleTrueOrderByDisplayOrderAsc(7L))
                .thenReturn(List.of());

        when(menuSubsectionRepository
                .findByMenuSectionIdAndActiveTrueOrderByDisplayOrderAscIdAsc(7L))
                .thenReturn(List.of());

        PizzaSize small = new PizzaSize();
        small.setMenuSection(pizzaSection);
        small.setName("Small");
        small.setBasePrice(new BigDecimal("13.00"));
        small.setDisplayOrder(0);
        small.setActive(true);

        when(pizzaSizeRepository
                .findByMenuSectionIdAndActiveTrueOrderByDisplayOrderAsc(7L))
                .thenReturn(List.of(small));

        PizzaTopping pepperoni = new PizzaTopping();
        pepperoni.setMenuSection(pizzaSection);
        pepperoni.setName("Pepperoni");
        pepperoni.setDisplayOrder(0);
        pepperoni.setActive(true);

        when(pizzaToppingRepository
                .findByMenuSectionIdAndActiveTrueOrderByDisplayOrderAsc(7L))
                .thenReturn(List.of(pepperoni));

        var response = publicMenuService.getMenu();

        var pizza = response.sections()
                .getFirst()
                .pizza();

        assertNotNull(pizza);

        assertEquals(
                1,
                pizza.toppings().size()
        );

        assertEquals(
                "Pepperoni",
                pizza.toppings().getFirst().name()
        );
    }

    @Test
    void getMenu_mapsActivePizzaAddOnsIntoSectionPizzaData() {

        MenuSection pizzaSection = mock(MenuSection.class);

        when(pizzaSection.getId())
                .thenReturn(7L);

        when(pizzaSection.getName())
                .thenReturn("Pizza");

        when(menuSectionRepository
                .findByActiveTrueOrderByDisplayOrderAsc())
                .thenReturn(List.of(pizzaSection));

        when(menuItemRepository
                .findByMenuSectionIdAndVisibleTrueOrderByDisplayOrderAsc(7L))
                .thenReturn(List.of());

        when(menuSubsectionRepository
                .findByMenuSectionIdAndActiveTrueOrderByDisplayOrderAscIdAsc(7L))
                .thenReturn(List.of());

        PizzaSize small = new PizzaSize();
        small.setMenuSection(pizzaSection);
        small.setName("Small");
        small.setBasePrice(new BigDecimal("13.00"));
        small.setDisplayOrder(0);
        small.setActive(true);

        when(pizzaSizeRepository
                .findByMenuSectionIdAndActiveTrueOrderByDisplayOrderAsc(7L))
                .thenReturn(List.of(small));

        PizzaAddOn doubleCheese = new PizzaAddOn();
        doubleCheese.setMenuSection(pizzaSection);
        doubleCheese.setName("Double Cheese");
        doubleCheese.setAmount(new BigDecimal("2.00"));
        doubleCheese.setType(PizzaAddOnType.EXTRA);
        doubleCheese.setDisplayOrder(0);
        doubleCheese.setActive(true);

        when(pizzaAddOnRepository
                .findByMenuSectionIdAndActiveTrueOrderByDisplayOrderAsc(7L))
                .thenReturn(List.of(doubleCheese));

        var response = publicMenuService.getMenu();

        var pizza = response.sections()
                .getFirst()
                .pizza();

        assertNotNull(pizza);

        assertEquals(
                1,
                pizza.addOns().size()
        );

        var addOn = pizza.addOns().getFirst();

        assertEquals(
                "Double Cheese",
                addOn.name()
        );

        assertEquals(
                new BigDecimal("2.00"),
                addOn.amount()
        );

        assertEquals(
                PizzaAddOnType.EXTRA,
                addOn.type()
        );
    }

    @Test
    void getMenu_mapsActiveCustomPizzaSpecialtyIntoSectionPizzaData() {

        MenuSection pizzaSection = mock(MenuSection.class);

        when(pizzaSection.getId())
                .thenReturn(7L);

        when(pizzaSection.getName())
                .thenReturn("Pizza");

        when(menuSectionRepository
                .findByActiveTrueOrderByDisplayOrderAsc())
                .thenReturn(List.of(pizzaSection));

        when(menuItemRepository
                .findByMenuSectionIdAndVisibleTrueOrderByDisplayOrderAsc(7L))
                .thenReturn(List.of());

        when(menuSubsectionRepository
                .findByMenuSectionIdAndActiveTrueOrderByDisplayOrderAscIdAsc(7L))
                .thenReturn(List.of());

        PizzaSize small = new PizzaSize();
        small.setMenuSection(pizzaSection);
        small.setName("Small");
        small.setBasePrice(new BigDecimal("13.00"));
        small.setDisplayOrder(0);
        small.setActive(true);

        when(pizzaSizeRepository
                .findByMenuSectionIdAndActiveTrueOrderByDisplayOrderAsc(7L))
                .thenReturn(List.of(small));

        Recipe worksRecipe = new Recipe("The Works");
        worksRecipe.setDescription("Our loaded specialty pizza.");
        worksRecipe.setImageUrl("https://example.com/works.jpg");
        worksRecipe.setImageAlt("The Works pizza");

        PizzaSpecialty specialty = new PizzaSpecialty();
        specialty.setMenuSection(pizzaSection);
        specialty.setRecipe(worksRecipe);
        specialty.setDisplayOrder(0);
        specialty.setActive(true);
        specialty.setPricingMode(
                PizzaSpecialtyPricingMode.CUSTOM
        );

        PizzaSpecialtyPrice specialtyPrice =
                new PizzaSpecialtyPrice();

        specialtyPrice.setPizzaSize(small);
        specialtyPrice.setAmount(
                new BigDecimal("29.00")
        );

        specialty.addPrice(specialtyPrice);

        when(pizzaSpecialtyRepository
                .findActiveByMenuSectionIdWithDetails(7L))
                .thenReturn(List.of(specialty));

        var response = publicMenuService.getMenu();

        var pizza = response.sections()
                .getFirst()
                .pizza();

        assertNotNull(pizza);

        assertEquals(
                1,
                pizza.specialties().size()
        );

        var publicSpecialty =
                pizza.specialties().getFirst();

        assertEquals(
                "The Works",
                publicSpecialty.name()
        );

        assertEquals(
                "Our loaded specialty pizza.",
                publicSpecialty.description()
        );

        assertEquals(
                PizzaSpecialtyPricingMode.CUSTOM,
                publicSpecialty.pricingMode()
        );

        assertEquals(
                1,
                publicSpecialty.prices().size()
        );

        assertEquals(
                "Small",
                publicSpecialty.prices()
                        .getFirst()
                        .sizeName()
        );

        assertEquals(
                new BigDecimal("29.00"),
                publicSpecialty.prices()
                        .getFirst()
                        .amount()
        );
    }

    @Test
    void getMenu_mapsPizzaSpecialtyToppingsAndAddOnPricingConfiguration() {

        MenuSection pizzaSection = mock(MenuSection.class);

        when(pizzaSection.getId())
                .thenReturn(7L);

        when(pizzaSection.getName())
                .thenReturn("Pizza");

        when(menuSectionRepository
                .findByActiveTrueOrderByDisplayOrderAsc())
                .thenReturn(List.of(pizzaSection));

        when(menuItemRepository
                .findByMenuSectionIdAndVisibleTrueOrderByDisplayOrderAsc(7L))
                .thenReturn(List.of());

        when(menuSubsectionRepository
                .findByMenuSectionIdAndActiveTrueOrderByDisplayOrderAscIdAsc(7L))
                .thenReturn(List.of());

        PizzaSize small = new PizzaSize();
        small.setMenuSection(pizzaSection);
        small.setName("Small");
        small.setBasePrice(new BigDecimal("13.00"));
        small.setDisplayOrder(0);
        small.setActive(true);

        when(pizzaSizeRepository
                .findByMenuSectionIdAndActiveTrueOrderByDisplayOrderAsc(7L))
                .thenReturn(List.of(small));

        Recipe recipe = new Recipe("The Works");

        PizzaSpecialty specialty = new PizzaSpecialty();
        specialty.setMenuSection(pizzaSection);
        specialty.setRecipe(recipe);
        specialty.setDisplayOrder(0);
        specialty.setActive(true);
        specialty.setPricingMode(
                PizzaSpecialtyPricingMode.CALCULATED
        );

        PizzaTopping pepperoni = new PizzaTopping();
        pepperoni.setMenuSection(pizzaSection);
        pepperoni.setName("Pepperoni");
        pepperoni.setDisplayOrder(0);
        pepperoni.setActive(true);

        specialty.getToppings().add(pepperoni);

        PizzaAddOn doubleCheese = new PizzaAddOn();
        doubleCheese.setMenuSection(pizzaSection);
        doubleCheese.setName("Double Cheese");
        doubleCheese.setAmount(new BigDecimal("2.00"));
        doubleCheese.setType(PizzaAddOnType.EXTRA);
        doubleCheese.setActive(true);

        PizzaSpecialtyAddOn freeDoubleCheese =
                new PizzaSpecialtyAddOn();

        freeDoubleCheese.setPizzaSpecialty(specialty);
        freeDoubleCheese.setPizzaAddOn(doubleCheese);
        freeDoubleCheese.setPricingType(
                PizzaSpecialtyAddOnPricingType.FREE
        );

        specialty.getSpecialtyAddOns()
                .add(freeDoubleCheese);

        PizzaAddOn anchovies = new PizzaAddOn();
        anchovies.setMenuSection(pizzaSection);
        anchovies.setName("Anchovies");
        anchovies.setAmount(new BigDecimal("2.00"));
        anchovies.setType(PizzaAddOnType.EXTRA);
        anchovies.setActive(true);

        PizzaSpecialtyAddOn customAnchovies =
                new PizzaSpecialtyAddOn();

        customAnchovies.setPizzaSpecialty(specialty);
        customAnchovies.setPizzaAddOn(anchovies);
        customAnchovies.setPricingType(
                PizzaSpecialtyAddOnPricingType.CUSTOM
        );
        customAnchovies.setOverrideAmount(
                new BigDecimal("1.00")
        );

        specialty.getSpecialtyAddOns()
                .add(customAnchovies);

        when(pizzaSpecialtyRepository
                .findActiveByMenuSectionIdWithDetails(7L))
                .thenReturn(List.of(specialty));

        var response = publicMenuService.getMenu();

        var publicSpecialty =
                response.sections()
                        .getFirst()
                        .pizza()
                        .specialties()
                        .getFirst();

        assertEquals(
                List.of("Pepperoni"),
                publicSpecialty.toppings()
        );

        assertEquals(
                2,
                publicSpecialty.addOns().size()
        );

        var freeAddOn =
                publicSpecialty.addOns().get(0);

        assertEquals(
                "Double Cheese",
                freeAddOn.name()
        );
        assertEquals(
                new BigDecimal("2.00"),
                freeAddOn.regularAmount()
        );
        assertEquals(
                PizzaSpecialtyAddOnPricingType.FREE,
                freeAddOn.pricingType()
        );
        assertNull(
                freeAddOn.overrideAmount()
        );

        var customAddOn =
                publicSpecialty.addOns().get(1);

        assertEquals(
                "Anchovies",
                customAddOn.name()
        );
        assertEquals(
                new BigDecimal("2.00"),
                customAddOn.regularAmount()
        );
        assertEquals(
                PizzaSpecialtyAddOnPricingType.CUSTOM,
                customAddOn.pricingType()
        );
        assertEquals(
                new BigDecimal("1.00"),
                customAddOn.overrideAmount()
        );
    }

    @Test
    void getMenu_calculatesPizzaSpecialtyPricesFromBasePriceAndToppingCharge() {

        MenuSection pizzaSection = mock(MenuSection.class);

        when(pizzaSection.getId())
                .thenReturn(7L);

        when(pizzaSection.getName())
                .thenReturn("Pizza");

        when(menuSectionRepository
                .findByActiveTrueOrderByDisplayOrderAsc())
                .thenReturn(List.of(pizzaSection));

        when(menuItemRepository
                .findByMenuSectionIdAndVisibleTrueOrderByDisplayOrderAsc(7L))
                .thenReturn(List.of());

        when(menuSubsectionRepository
                .findByMenuSectionIdAndActiveTrueOrderByDisplayOrderAscIdAsc(7L))
                .thenReturn(List.of());

        PizzaSize small = new PizzaSize();
        small.setMenuSection(pizzaSection);
        small.setName("Small");
        small.setBasePrice(new BigDecimal("13.00"));
        small.setDisplayOrder(0);
        small.setActive(true);

        PizzaSize medium = new PizzaSize();
        medium.setMenuSection(pizzaSection);
        medium.setName("Medium");
        medium.setBasePrice(new BigDecimal("16.00"));
        medium.setDisplayOrder(1);
        medium.setActive(true);

        when(pizzaSizeRepository
                .findByMenuSectionIdAndActiveTrueOrderByDisplayOrderAsc(7L))
                .thenReturn(List.of(
                        small,
                        medium
                ));

        PizzaAddOn toppingCharge = new PizzaAddOn();
        toppingCharge.setMenuSection(pizzaSection);
        toppingCharge.setName("Items");
        toppingCharge.setAmount(
                new BigDecimal("2.00")
        );
        toppingCharge.setType(
                PizzaAddOnType.TOPPING
        );
        toppingCharge.setActive(true);

        when(pizzaAddOnRepository
                .findByMenuSectionIdAndActiveTrueOrderByDisplayOrderAsc(7L))
                .thenReturn(List.of(toppingCharge));

        Recipe recipe =
                new Recipe("Three Item Special");

        PizzaSpecialty specialty =
                new PizzaSpecialty();

        specialty.setMenuSection(pizzaSection);
        specialty.setRecipe(recipe);
        specialty.setDisplayOrder(0);
        specialty.setActive(true);
        specialty.setPricingMode(
                PizzaSpecialtyPricingMode.CALCULATED
        );

        for (int index = 0; index < 3; index++) {

            PizzaTopping topping =
                    new PizzaTopping();

            topping.setMenuSection(pizzaSection);
            topping.setName(
                    "Topping " + index
            );
            topping.setDisplayOrder(index);
            topping.setActive(true);

            specialty.getToppings()
                    .add(topping);
        }

        when(pizzaSpecialtyRepository
                .findActiveByMenuSectionIdWithDetails(7L))
                .thenReturn(List.of(specialty));

        var response =
                publicMenuService.getMenu();

        var prices =
                response.sections()
                        .getFirst()
                        .pizza()
                        .specialties()
                        .getFirst()
                        .prices();

        assertEquals(
                2,
                prices.size()
        );

        assertEquals(
                "Small",
                prices.get(0).sizeName()
        );

        assertEquals(
                new BigDecimal("19.00"),
                prices.get(0).amount()
        );

        assertEquals(
                "Medium",
                prices.get(1).sizeName()
        );

        assertEquals(
                new BigDecimal("22.00"),
                prices.get(1).amount()
        );
    }

}