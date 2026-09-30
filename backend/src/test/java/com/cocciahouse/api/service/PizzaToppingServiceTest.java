package com.cocciahouse.api.service;

import com.cocciahouse.api.dto.menu.PizzaToppingRequest;
import com.cocciahouse.api.dto.menu.PizzaToppingResponse;
import com.cocciahouse.api.model.MenuSection;
import com.cocciahouse.api.model.PizzaTopping;
import com.cocciahouse.api.repository.MenuSectionRepository;
import com.cocciahouse.api.repository.PizzaToppingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PizzaToppingServiceTest {

    private PizzaToppingRepository pizzaToppingRepository;
    private MenuSectionRepository menuSectionRepository;
    private PizzaToppingService pizzaToppingService;

    @BeforeEach
    void setUp() {
        pizzaToppingRepository =
                mock(PizzaToppingRepository.class);

        menuSectionRepository =
                mock(MenuSectionRepository.class);

        pizzaToppingService =
                new PizzaToppingService(
                        pizzaToppingRepository,
                        menuSectionRepository
                );
    }

    @Test
    void createTopping_appendsAfterExistingToppings() {
        MenuSection section =
                new MenuSection("Pizza");

        PizzaTopping existing =
                new PizzaTopping();

        existing.setDisplayOrder(4);

        when(
                menuSectionRepository.findById(1L)
        ).thenReturn(
                Optional.of(section)
        );

        when(
                pizzaToppingRepository
                        .existsByMenuSectionIdAndNameIgnoreCase(
                                1L,
                                "Pepperoni"
                        )
        ).thenReturn(false);

        when(
                pizzaToppingRepository
                        .findByMenuSectionIdOrderByDisplayOrderAsc(
                                1L
                        )
        ).thenReturn(
                List.of(existing)
        );

        when(
                pizzaToppingRepository.save(
                        any(PizzaTopping.class)
                )
        ).thenAnswer(
                invocation ->
                        invocation.getArgument(0)
        );

        PizzaToppingRequest request =
                new PizzaToppingRequest(
                        "Pepperoni",
                        true
                );

        pizzaToppingService.createTopping(
                1L,
                request
        );

        ArgumentCaptor<PizzaTopping> captor =
                ArgumentCaptor.forClass(
                        PizzaTopping.class
                );

        verify(
                pizzaToppingRepository
        ).save(
                captor.capture()
        );

        PizzaTopping saved =
                captor.getValue();

        assertEquals(
                "Pepperoni",
                saved.getName()
        );

        assertEquals(
                5,
                saved.getDisplayOrder()
        );

        assertTrue(
                saved.isActive()
        );
    }

    @Test
    void createTopping_trimsName() {
        MenuSection section =
                new MenuSection("Pizza");

        when(
                menuSectionRepository.findById(1L)
        ).thenReturn(
                Optional.of(section)
        );

        when(
                pizzaToppingRepository
                        .existsByMenuSectionIdAndNameIgnoreCase(
                                1L,
                                "Sausage"
                        )
        ).thenReturn(false);

        when(
                pizzaToppingRepository
                        .findByMenuSectionIdOrderByDisplayOrderAsc(
                                1L
                        )
        ).thenReturn(
                List.of()
        );

        when(
                pizzaToppingRepository.save(
                        any(PizzaTopping.class)
                )
        ).thenAnswer(
                invocation ->
                        invocation.getArgument(0)
        );

        PizzaToppingRequest request =
                new PizzaToppingRequest(
                        "  Sausage  ",
                        true
                );

        pizzaToppingService.createTopping(
                1L,
                request
        );

        ArgumentCaptor<PizzaTopping> captor =
                ArgumentCaptor.forClass(
                        PizzaTopping.class
                );

        verify(
                pizzaToppingRepository
        ).save(
                captor.capture()
        );

        assertEquals(
                "Sausage",
                captor.getValue().getName()
        );
    }

    @Test
    void createTopping_rejectsDuplicateName() {
        MenuSection section =
                new MenuSection("Pizza");

        when(
                menuSectionRepository.findById(1L)
        ).thenReturn(
                Optional.of(section)
        );

        when(
                pizzaToppingRepository
                        .existsByMenuSectionIdAndNameIgnoreCase(
                                1L,
                                "Pepperoni"
                        )
        ).thenReturn(true);

        PizzaToppingRequest request =
                new PizzaToppingRequest(
                        "Pepperoni",
                        true
                );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                pizzaToppingService
                                        .createTopping(
                                                1L,
                                                request
                                        )
                );

        assertEquals(
                "Pizza topping already exists: Pepperoni",
                exception.getMessage()
        );

        verify(
                pizzaToppingRepository,
                never()
        ).save(
                any(PizzaTopping.class)
        );
    }

    @Test
    void updateTopping_updatesNameAndActiveWithoutChangingOrder() {
        MenuSection section =
                new MenuSection("Pizza");

        PizzaTopping topping =
                new PizzaTopping();

        topping.setMenuSection(section);
        topping.setName("Hot Peppers");
        topping.setDisplayOrder(3);
        topping.setActive(true);

        when(
                menuSectionRepository.existsById(1L)
        ).thenReturn(true);

        when(
                pizzaToppingRepository
                        .findByIdAndMenuSectionId(
                                5L,
                                1L
                        )
        ).thenReturn(
                Optional.of(topping)
        );

        when(
                pizzaToppingRepository
                        .existsByMenuSectionIdAndNameIgnoreCaseAndIdNot(
                                1L,
                                "Hot Banana Peppers",
                                5L
                        )
        ).thenReturn(false);

        when(
                pizzaToppingRepository.save(topping)
        ).thenReturn(topping);

        PizzaToppingRequest request =
                new PizzaToppingRequest(
                        "Hot Banana Peppers",
                        false
                );

        PizzaToppingResponse response =
                pizzaToppingService.updateTopping(
                        1L,
                        5L,
                        request
                );

        assertEquals(
                "Hot Banana Peppers",
                response.name()
        );

        assertFalse(response.active());

        assertEquals(
                3,
                response.displayOrder()
        );
    }
}