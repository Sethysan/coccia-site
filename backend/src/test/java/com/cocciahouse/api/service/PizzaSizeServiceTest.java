package com.cocciahouse.api.service;

import com.cocciahouse.api.dto.menu.PizzaSizeRequest;
import com.cocciahouse.api.dto.menu.PizzaSizeResponse;
import com.cocciahouse.api.model.MenuSection;
import com.cocciahouse.api.model.PizzaSize;
import com.cocciahouse.api.repository.MenuSectionRepository;
import com.cocciahouse.api.repository.PizzaSizeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PizzaSizeServiceTest {

    private PizzaSizeRepository pizzaSizeRepository;
    private MenuSectionRepository menuSectionRepository;
    private PizzaSizeService pizzaSizeService;

    @BeforeEach
    void setUp() {
        pizzaSizeRepository = mock(PizzaSizeRepository.class);
        menuSectionRepository = mock(MenuSectionRepository.class);

        pizzaSizeService = new PizzaSizeService(
                pizzaSizeRepository,
                menuSectionRepository
        );
    }

    @Test
    void createSize_appendsAfterExistingSizes() {
        MenuSection section = new MenuSection("Pizza");

        PizzaSize existing = new PizzaSize();
        existing.setDisplayOrder(2);

        when(menuSectionRepository.findById(1L))
                .thenReturn(Optional.of(section));

        when(
                pizzaSizeRepository
                        .existsByMenuSectionIdAndNameIgnoreCase(
                                1L,
                                "Large"
                        )
        ).thenReturn(false);

        when(
                pizzaSizeRepository
                        .findByMenuSectionIdOrderByDisplayOrderAsc(1L)
        ).thenReturn(List.of(existing));

        when(pizzaSizeRepository.save(any(PizzaSize.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PizzaSizeRequest request =
                new PizzaSizeRequest(
                        "Large",
                        new BigDecimal("19.00"),
                        true
                );

        pizzaSizeService.createSize(1L, request);

        ArgumentCaptor<PizzaSize> captor =
                ArgumentCaptor.forClass(PizzaSize.class);

        verify(pizzaSizeRepository).save(captor.capture());

        PizzaSize saved = captor.getValue();

        assertEquals("Large", saved.getName());
        assertEquals(new BigDecimal("19.00"), saved.getBasePrice());
        assertEquals(3, saved.getDisplayOrder());
        assertTrue(saved.isActive());
    }

    @Test
    void createSize_trimsName() {
        MenuSection section = new MenuSection("Pizza");

        when(menuSectionRepository.findById(1L))
                .thenReturn(Optional.of(section));

        when(
                pizzaSizeRepository
                        .existsByMenuSectionIdAndNameIgnoreCase(
                                1L,
                                "Small"
                        )
        ).thenReturn(false);

        when(
                pizzaSizeRepository
                        .findByMenuSectionIdOrderByDisplayOrderAsc(1L)
        ).thenReturn(List.of());

        when(pizzaSizeRepository.save(any(PizzaSize.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PizzaSizeRequest request =
                new PizzaSizeRequest(
                        "  Small  ",
                        new BigDecimal("13.00"),
                        true
                );

        pizzaSizeService.createSize(1L, request);

        ArgumentCaptor<PizzaSize> captor =
                ArgumentCaptor.forClass(PizzaSize.class);

        verify(pizzaSizeRepository).save(captor.capture());

        assertEquals(
                "Small",
                captor.getValue().getName()
        );
    }

    @Test
    void createSize_rejectsDuplicateName() {
        MenuSection section = new MenuSection("Pizza");

        when(menuSectionRepository.findById(1L))
                .thenReturn(Optional.of(section));

        when(
                pizzaSizeRepository
                        .existsByMenuSectionIdAndNameIgnoreCase(
                                1L,
                                "Small"
                        )
        ).thenReturn(true);

        PizzaSizeRequest request =
                new PizzaSizeRequest(
                        "Small",
                        new BigDecimal("13.00"),
                        true
                );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> pizzaSizeService.createSize(
                                1L,
                                request
                        )
                );

        assertEquals(
                "Pizza size already exists: Small",
                exception.getMessage()
        );

        verify(
                pizzaSizeRepository,
                never()
        ).save(any(PizzaSize.class));
    }

    @Test
    void updateSize_updatesEditableFields() {
        MenuSection section = new MenuSection("Pizza");

        PizzaSize pizzaSize = new PizzaSize();
        pizzaSize.setMenuSection(section);
        pizzaSize.setName("Medium");
        pizzaSize.setBasePrice(new BigDecimal("16.00"));
        pizzaSize.setDisplayOrder(1);
        pizzaSize.setActive(true);

        when(menuSectionRepository.existsById(1L))
                .thenReturn(true);

        when(
                pizzaSizeRepository.findByIdAndMenuSectionId(
                        5L,
                        1L
                )
        ).thenReturn(Optional.of(pizzaSize));

        when(
                pizzaSizeRepository
                        .existsByMenuSectionIdAndNameIgnoreCaseAndIdNot(
                                1L,
                                "Medium",
                                5L
                        )
        ).thenReturn(false);

        when(pizzaSizeRepository.save(pizzaSize))
                .thenReturn(pizzaSize);

        PizzaSizeRequest request =
                new PizzaSizeRequest(
                        "Medium",
                        new BigDecimal("17.50"),
                        false
                );

        PizzaSizeResponse response =
                pizzaSizeService.updateSize(
                        1L,
                        5L,
                        request
                );

        assertEquals(
                new BigDecimal("17.50"),
                response.basePrice()
        );

        assertFalse(response.active());
        assertEquals(1, response.displayOrder());
    }
}