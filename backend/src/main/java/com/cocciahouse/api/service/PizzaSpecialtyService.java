package com.cocciahouse.api.service;

import com.cocciahouse.api.dto.menu.PizzaSpecialtyPriceRequest;
import com.cocciahouse.api.dto.menu.PizzaSpecialtyPriceResponse;
import com.cocciahouse.api.dto.menu.PizzaSpecialtyRequest;
import com.cocciahouse.api.dto.menu.PizzaSpecialtyResponse;
import com.cocciahouse.api.model.MenuSection;
import com.cocciahouse.api.model.PizzaSize;
import com.cocciahouse.api.model.PizzaSpecialty;
import com.cocciahouse.api.model.PizzaSpecialtyPrice;
import com.cocciahouse.api.model.Recipe;
import com.cocciahouse.api.repository.MenuSectionRepository;
import com.cocciahouse.api.repository.PizzaSizeRepository;
import com.cocciahouse.api.repository.PizzaSpecialtyRepository;
import com.cocciahouse.api.repository.RecipeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PizzaSpecialtyService {

    private final PizzaSpecialtyRepository pizzaSpecialtyRepository;
    private final MenuSectionRepository menuSectionRepository;
    private final RecipeRepository recipeRepository;
    private final PizzaSizeRepository pizzaSizeRepository;

    public PizzaSpecialtyService(
            PizzaSpecialtyRepository pizzaSpecialtyRepository,
            MenuSectionRepository menuSectionRepository,
            RecipeRepository recipeRepository,
            PizzaSizeRepository pizzaSizeRepository
    ) {
        this.pizzaSpecialtyRepository = pizzaSpecialtyRepository;
        this.menuSectionRepository = menuSectionRepository;
        this.recipeRepository = recipeRepository;
        this.pizzaSizeRepository = pizzaSizeRepository;
    }

    @Transactional(readOnly = true)
    public List<PizzaSpecialtyResponse> getSpecialties(
            Long menuSectionId
    ) {
        ensureSectionExists(menuSectionId);

        return pizzaSpecialtyRepository
                .findByMenuSectionIdWithDetails(menuSectionId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PizzaSpecialtyResponse> getActiveSpecialties(
            Long menuSectionId
    ) {
        ensureSectionExists(menuSectionId);

        return pizzaSpecialtyRepository
                .findActiveByMenuSectionIdWithDetails(menuSectionId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public PizzaSpecialtyResponse createSpecialty(
            Long menuSectionId,
            PizzaSpecialtyRequest request
    ) {
        MenuSection menuSection =
                menuSectionRepository.findById(menuSectionId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Menu section not found: "
                                                + menuSectionId
                                )
                        );

        Recipe recipe =
                recipeRepository.findById(request.recipeId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Recipe not found: "
                                                + request.recipeId()
                                )
                        );

        if (!recipe.isActive()) {
            throw new IllegalArgumentException(
                    "Inactive recipe cannot be added as a specialty pizza: "
                            + recipe.getName()
            );
        }

        if (pizzaSpecialtyRepository
                .existsByMenuSectionIdAndRecipeId(
                        menuSectionId,
                        request.recipeId()
                )) {

            throw new IllegalArgumentException(
                    "Recipe is already a specialty pizza in this section: "
                            + recipe.getName()
            );
        }

        List<PizzaSpecialty> existingSpecialties =
                pizzaSpecialtyRepository
                        .findByMenuSectionIdWithDetails(
                                menuSectionId
                        );

        int nextDisplayOrder =
                existingSpecialties.stream()
                        .mapToInt(
                                PizzaSpecialty::getDisplayOrder
                        )
                        .max()
                        .orElse(-1)
                        + 1;

        PizzaSpecialty specialty =
                new PizzaSpecialty();

        specialty.setMenuSection(menuSection);
        specialty.setRecipe(recipe);
        specialty.setDisplayOrder(nextDisplayOrder);
        specialty.setActive(request.active());

        applyPrices(
                specialty,
                menuSectionId,
                request.prices()
        );

        PizzaSpecialty saved =
                pizzaSpecialtyRepository.save(specialty);

        return toResponse(saved);
    }

    @Transactional
    public PizzaSpecialtyResponse updateSpecialty(
            Long menuSectionId,
            Long specialtyId,
            PizzaSpecialtyRequest request
    ) {
        ensureSectionExists(menuSectionId);

        PizzaSpecialty specialty =
                pizzaSpecialtyRepository
                        .findByIdAndMenuSectionIdWithDetails(
                                specialtyId,
                                menuSectionId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Pizza specialty not found: "
                                                + specialtyId
                                )
                        );

        Recipe requestedRecipe =
                recipeRepository.findById(
                                request.recipeId()
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Recipe not found: "
                                                + request.recipeId()
                                )
                        );

        boolean recipeChanged =
                !specialty.getRecipe()
                        .getId()
                        .equals(request.recipeId());

        /*
         * An existing specialty may still reference a Recipe that
         * has since been deactivated.
         *
         * We allow editing its Pizza-specific settings as long as
         * the Recipe itself is not being changed.
         */
        if (recipeChanged && !requestedRecipe.isActive()) {
            throw new IllegalArgumentException(
                    "Inactive recipe cannot be selected as a specialty pizza: "
                            + requestedRecipe.getName()
            );
        }

        if (pizzaSpecialtyRepository
                .existsByMenuSectionIdAndRecipeIdAndIdNot(
                        menuSectionId,
                        request.recipeId(),
                        specialtyId
                )) {

            throw new IllegalArgumentException(
                    "Recipe is already a specialty pizza in this section: "
                            + requestedRecipe.getName()
            );
        }

        specialty.setRecipe(requestedRecipe);
        specialty.setActive(request.active());

        /*
         * Prices belong entirely to the specialty, so replacing
         * the collection is safe. orphanRemoval deletes the old
         * rows when the transaction commits.
         */
        specialty.clearPrices();

        applyPrices(
                specialty,
                menuSectionId,
                request.prices()
        );

        PizzaSpecialty saved =
                pizzaSpecialtyRepository.save(specialty);

        return toResponse(saved);
    }

    private void applyPrices(
            PizzaSpecialty specialty,
            Long menuSectionId,
            List<PizzaSpecialtyPriceRequest> priceRequests
    ) {
        Set<Long> usedSizeIds =
                new HashSet<>();

        for (PizzaSpecialtyPriceRequest priceRequest
                : priceRequests) {

            Long pizzaSizeId =
                    priceRequest.pizzaSizeId();

            if (!usedSizeIds.add(pizzaSizeId)) {
                throw new IllegalArgumentException(
                        "Pizza size appears more than once in specialty prices: "
                                + pizzaSizeId
                );
            }

            PizzaSize pizzaSize =
                    pizzaSizeRepository
                            .findByIdAndMenuSectionId(
                                    pizzaSizeId,
                                    menuSectionId
                            )
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Pizza size "
                                                    + pizzaSizeId
                                                    + " does not belong to menu section "
                                                    + menuSectionId
                                    )
                            );

            PizzaSpecialtyPrice price =
                    new PizzaSpecialtyPrice();

            price.setPizzaSize(pizzaSize);
            price.setAmount(priceRequest.amount());

            specialty.addPrice(price);
        }
    }

    private void ensureSectionExists(
            Long menuSectionId
    ) {
        if (!menuSectionRepository
                .existsById(menuSectionId)) {

            throw new IllegalArgumentException(
                    "Menu section not found: "
                            + menuSectionId
            );
        }
    }

    private PizzaSpecialtyResponse toResponse(
            PizzaSpecialty specialty
    ) {
        Recipe recipe =
                specialty.getRecipe();

        List<PizzaSpecialtyPriceResponse> prices =
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
                                new PizzaSpecialtyPriceResponse(
                                        price.getId(),
                                        price.getPizzaSize()
                                                .getId(),
                                        price.getPizzaSize()
                                                .getName(),
                                        price.getAmount()
                                )
                        )
                        .toList();

        return new PizzaSpecialtyResponse(
                specialty.getId(),
                specialty.getMenuSection().getId(),
                recipe.getId(),
                recipe.getName(),
                recipe.getDescription(),
                recipe.getImageUrl(),
                recipe.getImageAlt(),
                specialty.getDisplayOrder(),
                specialty.isActive(),
                prices
        );
    }
}