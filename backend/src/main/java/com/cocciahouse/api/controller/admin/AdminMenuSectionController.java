package com.cocciahouse.api.controller.admin;

import com.cocciahouse.api.dto.menu.MenuSectionRequest;
import com.cocciahouse.api.dto.menu.MenuSectionResponse;
import com.cocciahouse.api.model.MenuSection;
import com.cocciahouse.api.service.MenuSectionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/menu/sections")
public class AdminMenuSectionController {

    private final MenuSectionService menuSectionService;

    public AdminMenuSectionController(
            MenuSectionService menuSectionService
    ) {
        this.menuSectionService = menuSectionService;
    }

    @GetMapping
    public List<MenuSectionResponse> getAllSections() {

        return menuSectionService
                .getAllSections()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MenuSectionResponse createSection(
            @Valid
            @RequestBody
            MenuSectionRequest request
    ) {

        MenuSection section =
                menuSectionService
                        .createSection(request);

        return toResponse(section);
    }

    @PutMapping("/{id}")
    public MenuSectionResponse updateSection(
            @PathVariable Long id,
            @Valid
            @RequestBody
            MenuSectionRequest request
    ) {

        MenuSection section =
                menuSectionService
                        .updateSection(
                                id,
                                request
                        );

        return toResponse(section);
    }

    private MenuSectionResponse toResponse(
            MenuSection section
    ) {

        return new MenuSectionResponse(
                section.getId(),
                section.getName(),
                section.getSubtitle(),
                section.getFooterText(),
                section.getDisplayOrder(),
                section.isActive()
        );
    }
}