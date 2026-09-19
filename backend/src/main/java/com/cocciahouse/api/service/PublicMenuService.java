package com.cocciahouse.api.service;

import com.cocciahouse.api.dto.menu.PublicMenuResponse;
import com.cocciahouse.api.dto.menu.PublicMenuSectionResponse;
import com.cocciahouse.api.dto.menu.PublicMenuItemResponse;
import com.cocciahouse.api.dto.menu.PublicMenuSubsectionResponse;
import com.cocciahouse.api.dto.menu.PublicPizzaResponse;
import com.cocciahouse.api.dto.menu.PublicPizzaSizeResponse;
import com.cocciahouse.api.dto.menu.PublicPizzaToppingResponse;
import com.cocciahouse.api.dto.menu.PublicPizzaAddOnResponse;
import com.cocciahouse.api.dto.menu.PublicPizzaItemPriceResponse;
import com.cocciahouse.api.dto.menu.PublicPizzaSpecialtyResponse;
import com.cocciahouse.api.dto.menu.PublicPizzaSpecialtyAddOnResponse;
import com.cocciahouse.api.model.MenuSubsection;
import com.cocciahouse.api.repository.MenuSubsectionRepository;
import com.cocciahouse.api.repository.PizzaSizeRepository;
import com.cocciahouse.api.repository.PizzaToppingRepository;
import com.cocciahouse.api.repository.PizzaAddOnRepository;
import com.cocciahouse.api.repository.PizzaSpecialtyRepository;
import com.cocciahouse.api.model.MenuItem;
import com.cocciahouse.api.model.PizzaAddOnType;
import com.cocciahouse.api.model.PizzaSpecialtyPricingMode;
import com.cocciahouse.api.repository.MenuItemRepository;
import com.cocciahouse.api.model.MenuSection;
import com.cocciahouse.api.repository.MenuSectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

import java.util.List;

@Service
public class PublicMenuService {

    private final MenuSectionRepository menuSectionRepository;
    private final MenuItemRepository menuItemRepository;
    private final MenuSubsectionRepository menuSubsectionRepository;

    private final PizzaSizeRepository pizzaSizeRepository;
    private final PizzaToppingRepository pizzaToppingRepository;
    private final PizzaAddOnRepository pizzaAddOnRepository;
    private final PizzaSpecialtyRepository pizzaSpecialtyRepository;

    public PublicMenuService(
            MenuSectionRepository menuSectionRepository,
            MenuItemRepository menuItemRepository,
            MenuSubsectionRepository menuSubsectionRepository,
            PizzaSizeRepository pizzaSizeRepository,
            PizzaToppingRepository pizzaToppingRepository,
            PizzaAddOnRepository pizzaAddOnRepository,
            PizzaSpecialtyRepository pizzaSpecialtyRepository
    ) {
        this.menuSectionRepository = menuSectionRepository;
        this.menuItemRepository = menuItemRepository;
        this.menuSubsectionRepository = menuSubsectionRepository;
        this.pizzaSizeRepository = pizzaSizeRepository;
        this.pizzaToppingRepository = pizzaToppingRepository;
        this.pizzaAddOnRepository = pizzaAddOnRepository;
        this.pizzaSpecialtyRepository = pizzaSpecialtyRepository;
    }

    @Transactional(readOnly = true)
    public PublicMenuResponse getMenu() {

        List<PublicMenuSectionResponse> sections =
                menuSectionRepository
                        .findByActiveTrueOrderByDisplayOrderAsc()
                        .stream()
                        .map(this::mapSection)
                        .toList();

        return new PublicMenuResponse(sections);
    }

    private List<PublicPizzaSpecialtyResponse> mapPizzaSpecialties(
            MenuSection section,
            List<PublicPizzaSizeResponse> sizes,
            List<PublicPizzaAddOnResponse> pizzaAddOns
    ) {

        return pizzaSpecialtyRepository
                .findActiveByMenuSectionIdWithDetails(
                        section.getId()
                )
                .stream()
                .map(specialty -> {

                    List<PublicPizzaItemPriceResponse> prices;

                    if (specialty.getPricingMode()
                            == PizzaSpecialtyPricingMode.CALCULATED) {

                        BigDecimal toppingCharge =
                                pizzaAddOns.stream()
                                        .filter(addOn ->
                                                addOn.type()
                                                        == PizzaAddOnType.TOPPING
                                        )
                                        .map(
                                                PublicPizzaAddOnResponse::amount
                                        )
                                        .findFirst()
                                        .orElse(BigDecimal.ZERO);

                        BigDecimal toppingCount =
                                BigDecimal.valueOf(
                                        specialty.getToppings().size()
                                );

                        prices =
                                sizes.stream()
                                        .map(size ->
                                                new PublicPizzaItemPriceResponse(
                                                        size.name(),
                                                        size.basePrice().add(
                                                                toppingCharge.multiply(
                                                                        toppingCount
                                                                )
                                                        )
                                                )
                                        )
                                        .toList();

                    } else {

                        prices =
                                specialty.getPrices()
                                        .stream()
                                        .sorted(
                                                (left, right) ->
                                                        Integer.compare(
                                                                left.getPizzaSize()
                                                                        .getDisplayOrder(),
                                                                right.getPizzaSize()
                                                                        .getDisplayOrder()
                                                        )
                                        )
                                        .map(price ->
                                                new PublicPizzaItemPriceResponse(
                                                        price.getPizzaSize()
                                                                .getName(),
                                                        price.getAmount()
                                                )
                                        )
                                        .toList();
                    }

                    List<String> toppings =
                            specialty.getToppings()
                                    .stream()
                                    .sorted(
                                            (left, right) ->
                                                    Integer.compare(
                                                            left.getDisplayOrder(),
                                                            right.getDisplayOrder()
                                                    )
                                    )
                                    .map(topping ->
                                            topping.getName()
                                    )
                                    .toList();

                    List<PublicPizzaSpecialtyAddOnResponse> specialtyAddOns =
                            specialty.getSpecialtyAddOns()
                                    .stream()
                                    .map(specialtyAddOn ->
                                            new PublicPizzaSpecialtyAddOnResponse(
                                                    specialtyAddOn
                                                            .getPizzaAddOn()
                                                            .getName(),
                                                    specialtyAddOn
                                                            .getPizzaAddOn()
                                                            .getAmount(),
                                                    specialtyAddOn
                                                            .getPricingType(),
                                                    specialtyAddOn
                                                            .getOverrideAmount()
                                            )
                                    )
                                    .toList();

                    return new PublicPizzaSpecialtyResponse(
                            specialty.getRecipe().getName(),
                            specialty.getRecipe().getDescription(),
                            specialty.getRecipe().getImageUrl(),
                            specialty.getRecipe().getImageAlt(),
                            specialty.getPricingMode(),
                            toppings,
                            specialtyAddOns,
                            prices
                    );
                })
                .toList();
    }

    private PublicPizzaResponse mapPizza(
            MenuSection section
    ) {

        List<PublicPizzaSizeResponse> sizes =
                pizzaSizeRepository
                        .findByMenuSectionIdAndActiveTrueOrderByDisplayOrderAsc(
                                section.getId()
                        )
                        .stream()
                        .map(size ->
                                new PublicPizzaSizeResponse(
                                        size.getName(),
                                        size.getBasePrice()
                                )
                        )
                        .toList();

        if (sizes.isEmpty()) {
            return null;
        }

        List<PublicPizzaToppingResponse> toppings =
                pizzaToppingRepository
                        .findByMenuSectionIdAndActiveTrueOrderByDisplayOrderAsc(
                                section.getId()
                        )
                        .stream()
                        .map(topping ->
                                new PublicPizzaToppingResponse(
                                        topping.getName()
                                )
                        )
                        .toList();

        List<PublicPizzaAddOnResponse> addOns =
                pizzaAddOnRepository
                        .findByMenuSectionIdAndActiveTrueOrderByDisplayOrderAsc(
                                section.getId()
                        )
                        .stream()
                        .map(addOn ->
                                new PublicPizzaAddOnResponse(
                                        addOn.getName(),
                                        addOn.getAmount(),
                                        addOn.getType()
                                )
                        )
                        .toList();

        List<PublicPizzaSpecialtyResponse> specialties =
                mapPizzaSpecialties(
                        section,
                        sizes,
                        addOns
                );

        return new PublicPizzaResponse(
                sizes,
                toppings,
                addOns,
                specialties
        );
    }

    private PublicMenuSectionResponse mapSection(
            MenuSection section
    ) {

        List<MenuItem> sectionItems =
                menuItemRepository
                        .findByMenuSectionIdAndVisibleTrueOrderByDisplayOrderAsc(
                                section.getId()
                        );

        List<PublicMenuSubsectionResponse> subsections =
                menuSubsectionRepository
                        .findByMenuSectionIdAndActiveTrueOrderByDisplayOrderAscIdAsc(
                                section.getId()
                        )
                        .stream()
                        .map(subsection ->
                                mapSubsection(
                                        subsection,
                                        sectionItems
                                )
                        )
                        .toList();

        List<PublicMenuItemResponse> items =
                sectionItems
                        .stream()
                        .filter(item ->
                                item.getMenuSubsection() == null
                        )
                        .map(this::mapItem)
                        .toList();

        return new PublicMenuSectionResponse(
                section.getName(),
                section.getSubtitle(),
                section.getFooterText(),
                subsections,
                items,
                mapPizza(section)
        );
    }

    private PublicMenuSubsectionResponse mapSubsection(
            MenuSubsection subsection,
            List<MenuItem> sectionItems
    ) {

        List<PublicMenuItemResponse> items =
                sectionItems
                        .stream()
                        .filter(item ->
                                item.getMenuSubsection() != null
                                        && item.getMenuSubsection()
                                        .getId()
                                        .equals(subsection.getId())
                        )
                        .map(this::mapItem)
                        .toList();

        return new PublicMenuSubsectionResponse(
                subsection.getName(),
                subsection.getPrice(),
                items
        );
    }

    private PublicMenuItemResponse mapItem(
            MenuItem item
    ) {

        return new PublicMenuItemResponse(
                item.getRecipe().getName(),
                item.getRecipe().getDescription(),
                item.getRecipe().getImageUrl(),
                item.getRecipe().getImageAlt(),
                item.getPrices()
                        .stream()
                        .map(price ->
                                new com.cocciahouse.api.dto.menu.PublicMenuItemPriceResponse(
                                        price.getLabel(),
                                        price.getAmount()
                                )
                        )
                        .toList()
        );
    }

}