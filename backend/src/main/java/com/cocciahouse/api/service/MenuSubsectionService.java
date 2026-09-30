package com.cocciahouse.api.service;

import com.cocciahouse.api.dto.menu.MenuSubsectionRequest;
import com.cocciahouse.api.dto.menu.MenuSubsectionResponse;
import com.cocciahouse.api.exception.MenuSectionNotFoundException;
import com.cocciahouse.api.model.MenuSection;
import com.cocciahouse.api.model.MenuSubsection;
import com.cocciahouse.api.repository.MenuSectionRepository;
import com.cocciahouse.api.repository.MenuSubsectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MenuSubsectionService {

    private final MenuSubsectionRepository menuSubsectionRepository;
    private final MenuSectionRepository menuSectionRepository;

    public MenuSubsectionService(MenuSubsectionRepository menuSubsectionRepository, MenuSectionRepository menuSectionRepository) {
        this.menuSubsectionRepository = menuSubsectionRepository;
        this.menuSectionRepository = menuSectionRepository;
    }

    @Transactional(readOnly = true)
    public List<MenuSubsectionResponse> getSubsections(Long menuSectionId) {

        ensureSectionExists(menuSectionId);

        return menuSubsectionRepository.findByMenuSectionIdOrderByDisplayOrderAscIdAsc(menuSectionId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<MenuSubsectionResponse> getActiveSubsections(Long menuSectionId) {

        ensureSectionExists(menuSectionId);

        return menuSubsectionRepository.findByMenuSectionIdAndActiveTrueOrderByDisplayOrderAscIdAsc(menuSectionId).stream().map(this::toResponse).toList();
    }

    @Transactional
    public MenuSubsectionResponse createSubsection(Long menuSectionId, MenuSubsectionRequest request) {

        MenuSection menuSection = menuSectionRepository.findById(menuSectionId).orElseThrow(() -> new MenuSectionNotFoundException("Menu section not found."));

        String cleanedName = request.name().trim();

        if (menuSubsectionRepository.existsByMenuSectionIdAndNameIgnoreCase(menuSectionId, cleanedName)) {
            throw new IllegalArgumentException("A menu subsection with that name already exists.");
        }

        int nextDisplayOrder = menuSubsectionRepository.findByMenuSectionIdOrderByDisplayOrderAscIdAsc(menuSectionId).size();

        MenuSubsection subsection = new MenuSubsection(menuSection, cleanedName);
        subsection.setPrice(request.price());
        subsection.setDisplayOrder(nextDisplayOrder);

        subsection.setActive(request.active());

        return toResponse(menuSubsectionRepository.save(subsection));
    }

    @Transactional
    public MenuSubsectionResponse updateSubsection(Long menuSectionId, Long subsectionId, MenuSubsectionRequest request) {

        ensureSectionExists(menuSectionId);

        MenuSubsection subsection = menuSubsectionRepository.findByIdAndMenuSectionId(subsectionId, menuSectionId).orElseThrow(() -> new IllegalArgumentException("Menu subsection not found."));

        String cleanedName = request.name().trim();

        if (menuSubsectionRepository.existsByMenuSectionIdAndNameIgnoreCaseAndIdNot(menuSectionId, cleanedName, subsectionId)) {
            throw new IllegalArgumentException("A menu subsection with that name already exists.");
        }

        subsection.setName(cleanedName);

        subsection.setPrice(request.price());

        subsection.setActive(request.active());

        return toResponse(menuSubsectionRepository.save(subsection));
    }

    private void ensureSectionExists(Long menuSectionId) {

        if (!menuSectionRepository.existsById(menuSectionId)) {
            throw new MenuSectionNotFoundException("Menu section not found.");
        }
    }

    private MenuSubsectionResponse toResponse(MenuSubsection subsection) {

        return new MenuSubsectionResponse(
                subsection.getId(),
                subsection.getName(),
                subsection.getPrice(),
                subsection.getDisplayOrder(),
                subsection.isActive());
    }
}