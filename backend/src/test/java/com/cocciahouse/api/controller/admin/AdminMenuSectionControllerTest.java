package com.cocciahouse.api.controller.admin;

import com.cocciahouse.api.dto.menu.MenuSectionRequest;
import com.cocciahouse.api.dto.menu.MenuSectionResponse;
import com.cocciahouse.api.model.MenuSection;
import com.cocciahouse.api.service.MenuSectionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminMenuSectionControllerTest {

    @Mock
    private MenuSectionService menuSectionService;

    private AdminMenuSectionController controller;

    @BeforeEach
    void setUp() {
        controller =
                new AdminMenuSectionController(
                        menuSectionService
                );
    }

    @Test
    void getAllSections_returnsMappedResponses() {

        MenuSection starters =
                new MenuSection("Starters");

        starters.setSubtitle(
                "Start your meal with a favorite."
        );
        starters.setDisplayOrder(1);
        starters.setActive(true);

        MenuSection salads =
                new MenuSection("Salads");

        salads.setFooterText(
                "Ask about available dressings."
        );
        salads.setDisplayOrder(2);
        salads.setActive(false);

        when(
                menuSectionService.getAllSections()
        ).thenReturn(
                List.of(starters, salads)
        );

        List<MenuSectionResponse> result =
                controller.getAllSections();

        assertEquals(2, result.size());

        assertEquals(
                "Starters",
                result.getFirst().name()
        );

        assertEquals(
                "Start your meal with a favorite.",
                result.get(0).subtitle()
        );

        assertEquals(
                1,
                result.get(0).displayOrder()
        );

        assertEquals(
                "Salads",
                result.get(1).name()
        );

        assertEquals(
                "Ask about available dressings.",
                result.get(1).footerText()
        );

        assertFalse(result.get(1).active());
    }

    @Test
    void createSection_returnsCreatedSection() {

        MenuSectionRequest request =
                new MenuSectionRequest(
                        "Pasta",
                        "Served with salad and bread.",
                        null,
                        7,
                        true
                );

        MenuSection created =
                new MenuSection("Pasta");

        created.setSubtitle(
                "Served with salad and bread."
        );
        created.setDisplayOrder(7);
        created.setActive(true);

        when(
                menuSectionService.createSection(request)
        ).thenReturn(created);

        MenuSectionResponse result =
                controller.createSection(request);

        assertEquals(
                "Pasta",
                result.name()
        );

        assertEquals(
                "Served with salad and bread.",
                result.subtitle()
        );

        assertEquals(
                7,
                result.displayOrder()
        );

        assertTrue(result.active());
    }

    @Test
    void updateSection_returnsUpdatedSection() {

        Long sectionId = 7L;

        MenuSectionRequest request =
                new MenuSectionRequest(
                        "Pasta",
                        "Updated serving information.",
                        "Ask about pasta options.",
                        6,
                        false
                );

        MenuSection updated =
                new MenuSection("Pasta");

        updated.setSubtitle(
                "Updated serving information."
        );
        updated.setFooterText(
                "Ask about pasta options."
        );
        updated.setDisplayOrder(6);
        updated.setActive(false);

        when(
                menuSectionService.updateSection(
                        sectionId,
                        request
                )
        ).thenReturn(updated);

        MenuSectionResponse result =
                controller.updateSection(
                        sectionId,
                        request
                );

        assertEquals(
                "Pasta",
                result.name()
        );

        assertEquals(
                "Updated serving information.",
                result.subtitle()
        );

        assertEquals(
                "Ask about pasta options.",
                result.footerText()
        );

        assertEquals(
                6,
                result.displayOrder()
        );

        assertFalse(result.active());
    }
}