package com.cocciahouse.api.controller.admin;

import com.cocciahouse.api.dto.menu.MenuSubsectionRequest;
import com.cocciahouse.api.dto.menu.MenuSubsectionResponse;
import com.cocciahouse.api.service.MenuSubsectionService;
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
@RequestMapping(
        "/api/admin/menu/sections/{sectionId}/subsections"
)
public class AdminMenuSubsectionController {

    private final MenuSubsectionService menuSubsectionService;

    public AdminMenuSubsectionController(
            MenuSubsectionService menuSubsectionService
    ) {
        this.menuSubsectionService =
                menuSubsectionService;
    }

    @GetMapping
    public List<MenuSubsectionResponse> getSubsections(
            @PathVariable Long sectionId
    ) {

        return menuSubsectionService
                .getSubsections(sectionId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MenuSubsectionResponse createSubsection(
            @PathVariable Long sectionId,
            @Valid
            @RequestBody
            MenuSubsectionRequest request
    ) {

        return menuSubsectionService
                .createSubsection(
                        sectionId,
                        request
                );
    }

    @PutMapping("/{subsectionId}")
    public MenuSubsectionResponse updateSubsection(
            @PathVariable Long sectionId,
            @PathVariable Long subsectionId,
            @Valid
            @RequestBody
            MenuSubsectionRequest request
    ) {

        return menuSubsectionService
                .updateSubsection(
                        sectionId,
                        subsectionId,
                        request
                );
    }
}