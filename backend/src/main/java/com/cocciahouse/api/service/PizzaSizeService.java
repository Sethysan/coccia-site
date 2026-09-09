package com.cocciahouse.api.service;

import com.cocciahouse.api.dto.menu.PizzaSizeRequest;
import com.cocciahouse.api.dto.menu.PizzaSizeResponse;
import com.cocciahouse.api.model.MenuSection;
import com.cocciahouse.api.model.PizzaSize;
import com.cocciahouse.api.repository.MenuSectionRepository;
import com.cocciahouse.api.repository.PizzaSizeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PizzaSizeService {

    private final PizzaSizeRepository pizzaSizeRepository;
    private final MenuSectionRepository menuSectionRepository;

    public PizzaSizeService(
            PizzaSizeRepository pizzaSizeRepository,
            MenuSectionRepository menuSectionRepository
    ) {
        this.pizzaSizeRepository = pizzaSizeRepository;
        this.menuSectionRepository = menuSectionRepository;
    }

    @Transactional(readOnly = true)
    public List<PizzaSizeResponse> getSizes(Long menuSectionId) {
        ensureSectionExists(menuSectionId);

        return pizzaSizeRepository
                .findByMenuSectionIdOrderByDisplayOrderAsc(menuSectionId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PizzaSizeResponse> getActiveSizes(Long menuSectionId) {
        ensureSectionExists(menuSectionId);

        return pizzaSizeRepository
                .findByMenuSectionIdAndActiveTrueOrderByDisplayOrderAsc(
                        menuSectionId
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public PizzaSizeResponse createSize(
            Long menuSectionId,
            PizzaSizeRequest request
    ) {
        MenuSection menuSection =
                menuSectionRepository
                        .findById(menuSectionId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Menu section not found: "
                                                + menuSectionId
                                )
                        );

        String cleanName = request.name().trim();

        if (
                pizzaSizeRepository
                        .existsByMenuSectionIdAndNameIgnoreCase(
                                menuSectionId,
                                cleanName
                        )
        ) {
            throw new IllegalArgumentException(
                    "Pizza size already exists: " + cleanName
            );
        }

        List<PizzaSize> existingSizes =
                pizzaSizeRepository
                        .findByMenuSectionIdOrderByDisplayOrderAsc(
                                menuSectionId
                        );

        int nextDisplayOrder =
                existingSizes.stream()
                        .mapToInt(PizzaSize::getDisplayOrder)
                        .max()
                        .orElse(-1)
                        + 1;

        PizzaSize pizzaSize = new PizzaSize();

        pizzaSize.setMenuSection(menuSection);
        pizzaSize.setName(cleanName);
        pizzaSize.setBasePrice(request.basePrice());
        pizzaSize.setDisplayOrder(nextDisplayOrder);
        pizzaSize.setActive(request.active());

        PizzaSize saved =
                pizzaSizeRepository.save(pizzaSize);

        return toResponse(saved);
    }

    @Transactional
    public PizzaSizeResponse updateSize(
            Long menuSectionId,
            Long pizzaSizeId,
            PizzaSizeRequest request
    ) {
        ensureSectionExists(menuSectionId);

        PizzaSize pizzaSize =
                pizzaSizeRepository
                        .findByIdAndMenuSectionId(
                                pizzaSizeId,
                                menuSectionId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Pizza size not found: "
                                                + pizzaSizeId
                                )
                        );

        String cleanName = request.name().trim();

        if (
                pizzaSizeRepository
                        .existsByMenuSectionIdAndNameIgnoreCaseAndIdNot(
                                menuSectionId,
                                cleanName,
                                pizzaSizeId
                        )
        ) {
            throw new IllegalArgumentException(
                    "Pizza size already exists: " + cleanName
            );
        }

        pizzaSize.setName(cleanName);
        pizzaSize.setBasePrice(request.basePrice());
        pizzaSize.setActive(request.active());

        PizzaSize saved =
                pizzaSizeRepository.save(pizzaSize);

        return toResponse(saved);
    }

    private void ensureSectionExists(Long menuSectionId) {
        if (!menuSectionRepository.existsById(menuSectionId)) {
            throw new IllegalArgumentException(
                    "Menu section not found: " + menuSectionId
            );
        }
    }

    private PizzaSizeResponse toResponse(
            PizzaSize pizzaSize
    ) {
        return new PizzaSizeResponse(
                pizzaSize.getId(),
                pizzaSize.getMenuSection().getId(),
                pizzaSize.getName(),
                pizzaSize.getBasePrice(),
                pizzaSize.getDisplayOrder(),
                pizzaSize.isActive()
        );
    }
}