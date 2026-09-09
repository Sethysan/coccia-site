package com.cocciahouse.api.service;

import com.cocciahouse.api.dto.menu.PizzaToppingRequest;
import com.cocciahouse.api.dto.menu.PizzaToppingResponse;
import com.cocciahouse.api.model.MenuSection;
import com.cocciahouse.api.model.PizzaTopping;
import com.cocciahouse.api.repository.MenuSectionRepository;
import com.cocciahouse.api.repository.PizzaToppingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PizzaToppingService {

    private final PizzaToppingRepository pizzaToppingRepository;
    private final MenuSectionRepository menuSectionRepository;

    public PizzaToppingService(
            PizzaToppingRepository pizzaToppingRepository,
            MenuSectionRepository menuSectionRepository
    ) {
        this.pizzaToppingRepository = pizzaToppingRepository;
        this.menuSectionRepository = menuSectionRepository;
    }

    @Transactional(readOnly = true)
    public List<PizzaToppingResponse> getToppings(
            Long menuSectionId
    ) {
        ensureSectionExists(menuSectionId);

        return pizzaToppingRepository
                .findByMenuSectionIdOrderByDisplayOrderAsc(
                        menuSectionId
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PizzaToppingResponse> getActiveToppings(
            Long menuSectionId
    ) {
        ensureSectionExists(menuSectionId);

        return pizzaToppingRepository
                .findByMenuSectionIdAndActiveTrueOrderByDisplayOrderAsc(
                        menuSectionId
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public PizzaToppingResponse createTopping(
            Long menuSectionId,
            PizzaToppingRequest request
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
                pizzaToppingRepository
                        .existsByMenuSectionIdAndNameIgnoreCase(
                                menuSectionId,
                                cleanName
                        )
        ) {
            throw new IllegalArgumentException(
                    "Pizza topping already exists: "
                            + cleanName
            );
        }

        List<PizzaTopping> existingToppings =
                pizzaToppingRepository
                        .findByMenuSectionIdOrderByDisplayOrderAsc(
                                menuSectionId
                        );

        int nextDisplayOrder =
                existingToppings.stream()
                        .mapToInt(PizzaTopping::getDisplayOrder)
                        .max()
                        .orElse(-1)
                        + 1;

        PizzaTopping pizzaTopping =
                new PizzaTopping();

        pizzaTopping.setMenuSection(menuSection);
        pizzaTopping.setName(cleanName);
        pizzaTopping.setDisplayOrder(nextDisplayOrder);
        pizzaTopping.setActive(request.active());

        PizzaTopping saved =
                pizzaToppingRepository.save(pizzaTopping);

        return toResponse(saved);
    }

    @Transactional
    public PizzaToppingResponse updateTopping(
            Long menuSectionId,
            Long pizzaToppingId,
            PizzaToppingRequest request
    ) {
        ensureSectionExists(menuSectionId);

        PizzaTopping pizzaTopping =
                pizzaToppingRepository
                        .findByIdAndMenuSectionId(
                                pizzaToppingId,
                                menuSectionId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Pizza topping not found: "
                                                + pizzaToppingId
                                )
                        );

        String cleanName =
                request.name().trim();

        if (
                pizzaToppingRepository
                        .existsByMenuSectionIdAndNameIgnoreCaseAndIdNot(
                                menuSectionId,
                                cleanName,
                                pizzaToppingId
                        )
        ) {
            throw new IllegalArgumentException(
                    "Pizza topping already exists: "
                            + cleanName
            );
        }

        pizzaTopping.setName(cleanName);
        pizzaTopping.setActive(request.active());

        PizzaTopping saved =
                pizzaToppingRepository.save(pizzaTopping);

        return toResponse(saved);
    }

    private void ensureSectionExists(
            Long menuSectionId
    ) {
        if (!menuSectionRepository.existsById(menuSectionId)) {
            throw new IllegalArgumentException(
                    "Menu section not found: "
                            + menuSectionId
            );
        }
    }

    private PizzaToppingResponse toResponse(
            PizzaTopping pizzaTopping
    ) {
        return new PizzaToppingResponse(
                pizzaTopping.getId(),
                pizzaTopping.getMenuSection().getId(),
                pizzaTopping.getName(),
                pizzaTopping.getDisplayOrder(),
                pizzaTopping.isActive()
        );
    }
}