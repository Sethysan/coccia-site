package com.cocciahouse.api.service;

import com.cocciahouse.api.dto.menu.MenuSectionRequest;
import com.cocciahouse.api.model.MenuSection;
import com.cocciahouse.api.repository.MenuSectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MenuSectionService {

    private final MenuSectionRepository menuSectionRepository;

    public MenuSectionService(
            MenuSectionRepository menuSectionRepository
    ) {
        this.menuSectionRepository = menuSectionRepository;
    }

    @Transactional(readOnly = true)
    public List<MenuSection> getAllSections() {
        return menuSectionRepository
                .findAllByOrderByDisplayOrderAsc();
    }

    @Transactional(readOnly = true)
    public List<MenuSection> getActiveSections() {
        return menuSectionRepository
                .findByActiveTrueOrderByDisplayOrderAsc();
    }

    @Transactional
    public MenuSection createSection(
            MenuSectionRequest request
    ) {

        String cleanedName =
                request.name().trim();

        if (
                menuSectionRepository
                        .existsByNameIgnoreCase(cleanedName)
        ) {
            throw new IllegalArgumentException(
                    "A menu section with that name already exists."
            );
        }

        MenuSection section =
                new MenuSection(cleanedName);

        section.setSubtitle(
                cleanNullableText(
                        request.subtitle()
                )
        );

        section.setFooterText(
                cleanNullableText(
                        request.footerText()
                )
        );

        section.setDisplayOrder(
                request.displayOrder()
        );

        section.setActive(
                request.active()
        );

        return menuSectionRepository.save(section);
    }

    @Transactional
    public MenuSection updateSection(
            Long id,
            MenuSectionRequest request
    ) {

        MenuSection section =
                menuSectionRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Menu section not found."
                                )
                        );

        String cleanedName =
                request.name().trim();

        if (
                menuSectionRepository
                        .existsByNameIgnoreCaseAndIdNot(
                                cleanedName,
                                id
                        )
        ) {
            throw new IllegalArgumentException(
                    "A menu section with that name already exists."
            );
        }

        section.setName(cleanedName);

        section.setSubtitle(
                cleanNullableText(
                        request.subtitle()
                )
        );

        section.setFooterText(
                cleanNullableText(
                        request.footerText()
                )
        );

        section.setDisplayOrder(
                request.displayOrder()
        );

        section.setActive(
                request.active()
        );

        return menuSectionRepository.save(section);
    }

    private String cleanNullableText(
            String value
    ) {

        if (value == null) {
            return null;
        }

        String cleaned =
                value.trim();

        return cleaned.isEmpty()
                ? null
                : cleaned;
    }
}