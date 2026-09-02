package com.cocciahouse.api.service;

import com.cocciahouse.api.dto.menu.MenuSectionRequest;
import com.cocciahouse.api.model.MenuSection;
import com.cocciahouse.api.repository.MenuSectionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuSectionServiceTest {

    @Mock
    private MenuSectionRepository menuSectionRepository;

    private MenuSectionService menuSectionService;

    @BeforeEach
    void setUp() {
        menuSectionService =
                new MenuSectionService(
                        menuSectionRepository
                );
    }

    @Test
    void getAllSections_returnsSectionsInRepositoryOrder() {

        MenuSection starters =
                new MenuSection("Starters");

        MenuSection salads =
                new MenuSection("Salads");

        when(
                menuSectionRepository
                        .findAllByOrderByDisplayOrderAsc()
        ).thenReturn(
                List.of(starters, salads)
        );

        List<MenuSection> result =
                menuSectionService.getAllSections();

        assertEquals(2, result.size());
        assertEquals(
                "Starters",
                result.get(0).getName()
        );
        assertEquals(
                "Salads",
                result.get(1).getName()
        );
    }

    @Test
    void createSection_createsCleanedSection() {

        MenuSectionRequest request =
                new MenuSectionRequest(
                        "  House Favorites  ",
                        "  Served with salad and bread.  ",
                        "   ",
                        3,
                        true
                );

        when(
                menuSectionRepository
                        .existsByNameIgnoreCase(
                                "House Favorites"
                        )
        ).thenReturn(false);

        when(
                menuSectionRepository.save(any(MenuSection.class))
        ).thenAnswer(invocation ->
                invocation.getArgument(0)
        );

        MenuSection result =
                menuSectionService
                        .createSection(request);

        assertEquals(
                "House Favorites",
                result.getName()
        );

        assertEquals(
                "Served with salad and bread.",
                result.getSubtitle()
        );

        assertNull(
                result.getFooterText()
        );

        assertEquals(
                3,
                result.getDisplayOrder()
        );

        assertTrue(
                result.isActive()
        );
    }

    @Test
    void createSection_rejectsDuplicateName() {

        MenuSectionRequest request =
                new MenuSectionRequest(
                        "Pasta",
                        null,
                        null,
                        7,
                        true
                );

        when(
                menuSectionRepository
                        .existsByNameIgnoreCase(
                                "Pasta"
                        )
        ).thenReturn(true);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                menuSectionService
                                        .createSection(request)
                );

        assertEquals(
                "A menu section with that name already exists.",
                exception.getMessage()
        );

        verify(
                menuSectionRepository,
                never()
        ).save(any());
    }

    @Test
    void updateSection_updatesExistingSection() {

        Long sectionId = 4L;

        MenuSection existing =
                new MenuSection("Entrees");

        MenuSectionRequest request =
                new MenuSectionRequest(
                        "Entrees",
                        "  Served with salad and bread.  ",
                        "  Ask about today's selections.  ",
                        4,
                        false
                );

        when(
                menuSectionRepository.findById(sectionId)
        ).thenReturn(
                Optional.of(existing)
        );

        when(
                menuSectionRepository
                        .existsByNameIgnoreCaseAndIdNot(
                                "Entrees",
                                sectionId
                        )
        ).thenReturn(false);

        when(
                menuSectionRepository.save(existing)
        ).thenReturn(existing);

        MenuSection result =
                menuSectionService.updateSection(
                        sectionId,
                        request
                );

        assertEquals(
                "Served with salad and bread.",
                result.getSubtitle()
        );

        assertEquals(
                "Ask about today's selections.",
                result.getFooterText()
        );

        assertEquals(
                4,
                result.getDisplayOrder()
        );

        assertFalse(
                result.isActive()
        );
    }

    @Test
    void updateSection_throwsWhenSectionDoesNotExist() {

        Long sectionId = 999L;

        MenuSectionRequest request =
                new MenuSectionRequest(
                        "Missing",
                        null,
                        null,
                        1,
                        true
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
                                menuSectionService.updateSection(
                                        sectionId,
                                        request
                                )
                );

        assertEquals(
                "Menu section not found.",
                exception.getMessage()
        );
    }

    @Test
    void updateSection_rejectsDuplicateNameFromAnotherSection() {

        Long sectionId = 4L;

        MenuSection existing =
                new MenuSection("Entrees");

        MenuSectionRequest request =
                new MenuSectionRequest(
                        "Pasta",
                        null,
                        null,
                        4,
                        true
                );

        when(
                menuSectionRepository.findById(sectionId)
        ).thenReturn(
                Optional.of(existing)
        );

        when(
                menuSectionRepository
                        .existsByNameIgnoreCaseAndIdNot(
                                "Pasta",
                                sectionId
                        )
        ).thenReturn(true);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                menuSectionService.updateSection(
                                        sectionId,
                                        request
                                )
                );

        assertEquals(
                "A menu section with that name already exists.",
                exception.getMessage()
        );

        verify(
                menuSectionRepository,
                never()
        ).save(any());
    }

    @Test
    void getActiveSections_returnsOnlyActiveSectionsInRepositoryOrder() {

        MenuSection starters =
                new MenuSection("Starters");

        MenuSection salads =
                new MenuSection("Salads");

        when(
                menuSectionRepository
                        .findByActiveTrueOrderByDisplayOrderAsc()
        ).thenReturn(
                List.of(starters, salads)
        );

        List<MenuSection> result =
                menuSectionService.getActiveSections();

        assertEquals(2, result.size());

        assertEquals(
                "Starters",
                result.get(0).getName()
        );

        assertEquals(
                "Salads",
                result.get(1).getName()
        );
    }

}