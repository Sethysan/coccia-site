package com.cocciahouse.api.service;

import com.cocciahouse.api.dto.menu.PizzaAddOnRequest;
import com.cocciahouse.api.dto.menu.PizzaAddOnResponse;
import com.cocciahouse.api.model.MenuSection;
import com.cocciahouse.api.model.PizzaAddOn;
import com.cocciahouse.api.model.PizzaAddOnType;
import com.cocciahouse.api.repository.MenuSectionRepository;
import com.cocciahouse.api.repository.PizzaAddOnRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PizzaAddOnService {

    private final PizzaAddOnRepository pizzaAddOnRepository;
    private final MenuSectionRepository menuSectionRepository;

    public PizzaAddOnService(
            PizzaAddOnRepository pizzaAddOnRepository,
            MenuSectionRepository menuSectionRepository
    ) {
        this.pizzaAddOnRepository = pizzaAddOnRepository;
        this.menuSectionRepository = menuSectionRepository;
    }

    @Transactional(readOnly = true)
    public List<PizzaAddOnResponse> getAddOns(
            Long menuSectionId
    ) {
        ensureSectionExists(menuSectionId);

        return pizzaAddOnRepository
                .findByMenuSectionIdOrderByDisplayOrderAsc(
                        menuSectionId
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PizzaAddOnResponse> getActiveAddOns(
            Long menuSectionId
    ) {
        ensureSectionExists(menuSectionId);

        return pizzaAddOnRepository
                .findByMenuSectionIdAndActiveTrueOrderByDisplayOrderAsc(
                        menuSectionId
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public PizzaAddOnResponse createAddOn(
            Long menuSectionId,
            PizzaAddOnRequest request
    ) {
        MenuSection menuSection =
                menuSectionRepository
                        .findById(menuSectionId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Menu section not found: " + menuSectionId
                                )
                        );

        String cleanName = request.name().trim();

        if (
                pizzaAddOnRepository
                        .existsByMenuSectionIdAndNameIgnoreCase(
                                menuSectionId,
                                cleanName
                        )
        ) {
            throw new IllegalArgumentException(
                    "Pizza add-on already exists: " + cleanName
            );
        }

        ensureToppingRuleIsAvailable(
                menuSectionId,
                null,
                request.type(),
                request.active()
        );

        List<PizzaAddOn> existingAddOns =
                pizzaAddOnRepository
                        .findByMenuSectionIdOrderByDisplayOrderAsc(
                                menuSectionId
                        );

        int nextDisplayOrder =
                existingAddOns.stream()
                        .mapToInt(PizzaAddOn::getDisplayOrder)
                        .max()
                        .orElse(-1)
                        + 1;

        PizzaAddOn pizzaAddOn = new PizzaAddOn();

        pizzaAddOn.setMenuSection(menuSection);
        pizzaAddOn.setName(cleanName);
        pizzaAddOn.setAmount(request.amount());
        pizzaAddOn.setType(request.type());
        pizzaAddOn.setDisplayOrder(nextDisplayOrder);
        pizzaAddOn.setActive(request.active());

        PizzaAddOn saved =
                pizzaAddOnRepository.save(pizzaAddOn);

        return toResponse(saved);
    }

    @Transactional
    public PizzaAddOnResponse updateAddOn(
            Long menuSectionId,
            Long pizzaAddOnId,
            PizzaAddOnRequest request
    ) {
        ensureSectionExists(menuSectionId);

        PizzaAddOn pizzaAddOn =
                pizzaAddOnRepository
                        .findByIdAndMenuSectionId(
                                pizzaAddOnId,
                                menuSectionId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Pizza add-on not found: " + pizzaAddOnId
                                )
                        );

        String cleanName = request.name().trim();

        if (
                pizzaAddOnRepository
                        .existsByMenuSectionIdAndNameIgnoreCaseAndIdNot(
                                menuSectionId,
                                cleanName,
                                pizzaAddOnId
                        )
        ) {
            throw new IllegalArgumentException(
                    "Pizza add-on already exists: " + cleanName
            );
        }

        ensureToppingRuleIsAvailable(
                menuSectionId,
                pizzaAddOnId,
                request.type(),
                request.active()
        );

        pizzaAddOn.setName(cleanName);
        pizzaAddOn.setAmount(request.amount());
        pizzaAddOn.setType(request.type());
        pizzaAddOn.setActive(request.active());

        PizzaAddOn saved =
                pizzaAddOnRepository.save(pizzaAddOn);

        return toResponse(saved);
    }

    private void ensureToppingRuleIsAvailable(
            Long menuSectionId,
            Long currentAddOnId,
            PizzaAddOnType type,
            boolean active
    ) {
        if (
                type != PizzaAddOnType.TOPPING
                        || !active
        ) {
            return;
        }

        boolean toppingRuleExists;

        if (currentAddOnId == null) {
            toppingRuleExists =
                    pizzaAddOnRepository
                            .existsByMenuSectionIdAndTypeAndActiveTrue(
                                    menuSectionId,
                                    PizzaAddOnType.TOPPING
                            );
        } else {
            toppingRuleExists =
                    pizzaAddOnRepository
                            .existsByMenuSectionIdAndTypeAndActiveTrueAndIdNot(
                                    menuSectionId,
                                    PizzaAddOnType.TOPPING,
                                    currentAddOnId
                            );
        }

        if (toppingRuleExists) {
            throw new IllegalArgumentException(
                    "Only one active topping price rule "
                            + "is allowed per pizza section"
            );
        }
    }

    private void ensureSectionExists(
            Long menuSectionId
    ) {
        if (!menuSectionRepository.existsById(menuSectionId)) {
            throw new IllegalArgumentException(
                    "Menu section not found: " + menuSectionId
            );
        }
    }

    private PizzaAddOnResponse toResponse(
            PizzaAddOn pizzaAddOn
    ) {
        return new PizzaAddOnResponse(
                pizzaAddOn.getId(),
                pizzaAddOn.getMenuSection().getId(),
                pizzaAddOn.getName(),
                pizzaAddOn.getAmount(),
                pizzaAddOn.getType(),
                pizzaAddOn.getDisplayOrder(),
                pizzaAddOn.isActive()
        );
    }
}