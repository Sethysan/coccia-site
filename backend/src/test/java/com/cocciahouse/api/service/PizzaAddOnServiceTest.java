package com.cocciahouse.api.service;

import com.cocciahouse.api.dto.menu.PizzaAddOnRequest;
import com.cocciahouse.api.dto.menu.PizzaAddOnResponse;
import com.cocciahouse.api.model.MenuSection;
import com.cocciahouse.api.model.PizzaAddOn;
import com.cocciahouse.api.model.PizzaAddOnType;
import com.cocciahouse.api.repository.MenuSectionRepository;
import com.cocciahouse.api.repository.PizzaAddOnRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PizzaAddOnServiceTest {

    private PizzaAddOnRepository pizzaAddOnRepository;
    private MenuSectionRepository menuSectionRepository;
    private PizzaAddOnService pizzaAddOnService;

    @BeforeEach
    void setUp() {
        pizzaAddOnRepository =
                mock(PizzaAddOnRepository.class);

        menuSectionRepository =
                mock(MenuSectionRepository.class);

        pizzaAddOnService =
                new PizzaAddOnService(
                        pizzaAddOnRepository,
                        menuSectionRepository
                );
    }

    @Test
    void createAddOn_appendsAfterExistingAddOns() {
        MenuSection section =
                new MenuSection("Pizza");

        PizzaAddOn existing =
                new PizzaAddOn();

        existing.setDisplayOrder(2);

        when(
                menuSectionRepository.findById(1L)
        ).thenReturn(
                Optional.of(section)
        );

        when(
                pizzaAddOnRepository
                        .existsByMenuSectionIdAndNameIgnoreCase(
                                1L,
                                "Anchovies"
                        )
        ).thenReturn(false);

        when(
                pizzaAddOnRepository
                        .findByMenuSectionIdOrderByDisplayOrderAsc(
                                1L
                        )
        ).thenReturn(
                List.of(existing)
        );

        when(
                pizzaAddOnRepository.save(
                        any(PizzaAddOn.class)
                )
        ).thenAnswer(
                invocation ->
                        invocation.getArgument(0)
        );

        PizzaAddOnRequest request =
                new PizzaAddOnRequest(
                        "Anchovies",
                        new BigDecimal("2.00"),
                        PizzaAddOnType.EXTRA,
                        true
                );

        pizzaAddOnService.createAddOn(
                1L,
                request
        );

        ArgumentCaptor<PizzaAddOn> captor =
                ArgumentCaptor.forClass(
                        PizzaAddOn.class
                );

        verify(
                pizzaAddOnRepository
        ).save(
                captor.capture()
        );

        PizzaAddOn saved =
                captor.getValue();

        assertEquals(
                "Anchovies",
                saved.getName()
        );

        assertEquals(
                new BigDecimal("2.00"),
                saved.getAmount()
        );

        assertEquals(
                PizzaAddOnType.EXTRA,
                saved.getType()
        );

        assertEquals(
                3,
                saved.getDisplayOrder()
        );

        assertTrue(
                saved.isActive()
        );
    }

    @Test
    void createAddOn_rejectsSecondActiveToppingRule() {
        MenuSection section =
                new MenuSection("Pizza");

        when(
                menuSectionRepository.findById(1L)
        ).thenReturn(
                Optional.of(section)
        );

        when(
                pizzaAddOnRepository
                        .existsByMenuSectionIdAndNameIgnoreCase(
                                1L,
                                "Each Topping"
                        )
        ).thenReturn(false);

        when(
                pizzaAddOnRepository
                        .existsByMenuSectionIdAndTypeAndActiveTrue(
                                1L,
                                PizzaAddOnType.TOPPING
                        )
        ).thenReturn(true);

        PizzaAddOnRequest request =
                new PizzaAddOnRequest(
                        "Each Topping",
                        new BigDecimal("2.50"),
                        PizzaAddOnType.TOPPING,
                        true
                );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                pizzaAddOnService
                                        .createAddOn(
                                                1L,
                                                request
                                        )
                );

        assertEquals(
                "Only one active topping price rule "
                        + "is allowed per pizza section",
                exception.getMessage()
        );

        verify(
                pizzaAddOnRepository,
                never()
        ).save(
                any(PizzaAddOn.class)
        );
    }

    @Test
    void createAddOn_allowsInactiveToppingRule() {
        MenuSection section =
                new MenuSection("Pizza");

        when(
                menuSectionRepository.findById(1L)
        ).thenReturn(
                Optional.of(section)
        );

        when(
                pizzaAddOnRepository
                        .existsByMenuSectionIdAndNameIgnoreCase(
                                1L,
                                "Future Topping Price"
                        )
        ).thenReturn(false);

        when(
                pizzaAddOnRepository
                        .findByMenuSectionIdOrderByDisplayOrderAsc(
                                1L
                        )
        ).thenReturn(
                List.of()
        );

        when(
                pizzaAddOnRepository.save(
                        any(PizzaAddOn.class)
                )
        ).thenAnswer(
                invocation ->
                        invocation.getArgument(0)
        );

        PizzaAddOnRequest request =
                new PizzaAddOnRequest(
                        "Future Topping Price",
                        new BigDecimal("2.50"),
                        PizzaAddOnType.TOPPING,
                        false
                );

        pizzaAddOnService.createAddOn(
                1L,
                request
        );

        verify(
                pizzaAddOnRepository,
                never()
        ).existsByMenuSectionIdAndTypeAndActiveTrue(
                anyLong(),
                any()
        );

        verify(
                pizzaAddOnRepository
        ).save(
                any(PizzaAddOn.class)
        );
    }

    @Test
    void updateAddOn_allowsExistingToppingRuleToEditItself() {
        MenuSection section =
                new MenuSection("Pizza");

        PizzaAddOn pizzaAddOn =
                new PizzaAddOn();

        pizzaAddOn.setMenuSection(section);
        pizzaAddOn.setName(
                "Additional Item"
        );
        pizzaAddOn.setAmount(
                new BigDecimal("2.00")
        );
        pizzaAddOn.setType(
                PizzaAddOnType.TOPPING
        );
        pizzaAddOn.setDisplayOrder(0);
        pizzaAddOn.setActive(true);

        when(
                menuSectionRepository.existsById(1L)
        ).thenReturn(true);

        when(
                pizzaAddOnRepository
                        .findByIdAndMenuSectionId(
                                5L,
                                1L
                        )
        ).thenReturn(
                Optional.of(pizzaAddOn)
        );

        when(
                pizzaAddOnRepository
                        .existsByMenuSectionIdAndNameIgnoreCaseAndIdNot(
                                1L,
                                "Additional Item",
                                5L
                        )
        ).thenReturn(false);

        when(
                pizzaAddOnRepository
                        .existsByMenuSectionIdAndTypeAndActiveTrueAndIdNot(
                                1L,
                                PizzaAddOnType.TOPPING,
                                5L
                        )
        ).thenReturn(false);

        when(
                pizzaAddOnRepository.save(
                        pizzaAddOn
                )
        ).thenReturn(
                pizzaAddOn
        );

        PizzaAddOnRequest request =
                new PizzaAddOnRequest(
                        "Additional Item",
                        new BigDecimal("2.50"),
                        PizzaAddOnType.TOPPING,
                        true
                );

        PizzaAddOnResponse response =
                pizzaAddOnService.updateAddOn(
                        1L,
                        5L,
                        request
                );

        assertEquals(
                new BigDecimal("2.50"),
                response.amount()
        );

        assertEquals(
                PizzaAddOnType.TOPPING,
                response.type()
        );

        assertTrue(
                response.active()
        );
    }

    @Test
    void updateAddOn_rejectsChangingExtraIntoSecondActiveToppingRule() {
        MenuSection section =
                new MenuSection("Pizza");

        PizzaAddOn pizzaAddOn =
                new PizzaAddOn();

        pizzaAddOn.setMenuSection(section);
        pizzaAddOn.setName("Anchovies");
        pizzaAddOn.setAmount(
                new BigDecimal("2.00")
        );
        pizzaAddOn.setType(
                PizzaAddOnType.EXTRA
        );
        pizzaAddOn.setDisplayOrder(1);
        pizzaAddOn.setActive(true);

        when(
                menuSectionRepository.existsById(1L)
        ).thenReturn(true);

        when(
                pizzaAddOnRepository
                        .findByIdAndMenuSectionId(
                                5L,
                                1L
                        )
        ).thenReturn(
                Optional.of(pizzaAddOn)
        );

        when(
                pizzaAddOnRepository
                        .existsByMenuSectionIdAndNameIgnoreCaseAndIdNot(
                                1L,
                                "Anchovies",
                                5L
                        )
        ).thenReturn(false);

        when(
                pizzaAddOnRepository
                        .existsByMenuSectionIdAndTypeAndActiveTrueAndIdNot(
                                1L,
                                PizzaAddOnType.TOPPING,
                                5L
                        )
        ).thenReturn(true);

        PizzaAddOnRequest request =
                new PizzaAddOnRequest(
                        "Anchovies",
                        new BigDecimal("2.00"),
                        PizzaAddOnType.TOPPING,
                        true
                );

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        pizzaAddOnService.updateAddOn(
                                1L,
                                5L,
                                request
                        )
        );

        verify(
                pizzaAddOnRepository,
                never()
        ).save(
                any(PizzaAddOn.class)
        );
    }
}