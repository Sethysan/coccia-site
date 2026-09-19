package com.cocciahouse.api.controller.publicapi;

import com.cocciahouse.api.dto.menu.PublicMenuResponse;
import com.cocciahouse.api.dto.menu.PublicMenuSectionResponse;
import com.cocciahouse.api.service.PublicMenuService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PublicMenuControllerTest {

    @Mock
    private PublicMenuService publicMenuService;

    private PublicMenuController publicMenuController;

    @BeforeEach
    void setUp() {
        publicMenuController =
                new PublicMenuController(
                        publicMenuService
                );
    }

    @Test
    void getMenu_returnsPublicMenuResponse() {

        PublicMenuSectionResponse starters =
                new PublicMenuSectionResponse(
                        "Starters",
                        null,
                        null,
                        List.of(),
                        List.of(),
                        null
                );

        PublicMenuResponse menu =
                new PublicMenuResponse(
                        List.of(starters)
                );

        when(
                publicMenuService.getMenu()
        ).thenReturn(menu);

        ResponseEntity<PublicMenuResponse> response =
                publicMenuController.getMenu();

        assertEquals(
                200,
                response.getStatusCode().value()
        );

        assertEquals(
                menu,
                response.getBody()
        );

        verify(
                publicMenuService
        ).getMenu();
    }
}