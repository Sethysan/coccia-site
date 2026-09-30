package com.cocciahouse.api.controller.publicapi;

import com.cocciahouse.api.dto.menu.PublicMenuResponse;
import com.cocciahouse.api.service.PublicMenuService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/menu")
public class PublicMenuController {

    private final PublicMenuService publicMenuService;

    public PublicMenuController(
            PublicMenuService publicMenuService
    ) {
        this.publicMenuService = publicMenuService;
    }

    @GetMapping
    public ResponseEntity<PublicMenuResponse> getMenu() {

        return ResponseEntity.ok(
                publicMenuService.getMenu()
        );
    }
}