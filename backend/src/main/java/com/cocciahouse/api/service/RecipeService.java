package com.cocciahouse.api.service;

import com.cocciahouse.api.dto.weeklyOffering.WeeklyOfferingItemResponse;
import com.cocciahouse.api.mapper.WeeklyOfferingMapper;
import com.cocciahouse.api.model.Recipe;
import com.cocciahouse.api.model.WeeklyOfferingStatus;
import com.cocciahouse.api.repository.RecipeRepository;
import com.cocciahouse.api.repository.WeeklyOfferingItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import com.cocciahouse.api.exception.DuplicateRecipeException;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final WeeklyOfferingItemRepository weeklyOfferingItemRepository;
    private final WeeklyOfferingMapper weeklyOfferingMapper;

    public RecipeService(
            RecipeRepository recipeRepository,
            WeeklyOfferingItemRepository weeklyOfferingItemRepository,
            WeeklyOfferingMapper weeklyOfferingMapper
    ) {
        this.recipeRepository = recipeRepository;
        this.weeklyOfferingItemRepository = weeklyOfferingItemRepository;
        this.weeklyOfferingMapper = weeklyOfferingMapper;
    }

    public List<Recipe> getActiveRecipes() {
        return recipeRepository.findAllByActiveTrueOrderByNameAsc();
    }

    public List<Recipe> searchActiveRecipes(String search) {
        return recipeRepository.findByActiveTrueAndNameContainingIgnoreCaseOrderByNameAsc(search);
    }

    public Optional<Recipe> getByName(String name) {
        return recipeRepository.findByNameIgnoreCase(name);
    }

    public Recipe createRecipe(String name) {

        String cleanedName = name.trim();

        if (recipeRepository.existsByNameIgnoreCase(cleanedName)) {
            throw new DuplicateRecipeException("A recipe with that name already exists.");
        }

        Recipe recipe = new Recipe(cleanedName);

        return recipeRepository.save(recipe);
    }

    @Transactional(readOnly = true)
    public Optional<WeeklyOfferingItemResponse> getLatestOfferingItem(
            Long recipeId
    ) {
        return weeklyOfferingItemRepository
                .findFirstByRecipeIdAndWeeklyOfferingStatusInOrderByWeeklyOfferingStartDateDesc(
                        recipeId,
                        List.of(
                                WeeklyOfferingStatus.SCHEDULED,
                                WeeklyOfferingStatus.PUBLISHED
                        )
                )
                .map(weeklyOfferingMapper::toItemResponse);
    }

    @Transactional(readOnly = true)
    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAllByOrderByNameAsc();
    }

    @Transactional(readOnly = true)
    public List<Recipe> searchRecipes(String search) {
        return recipeRepository.findByNameContainingIgnoreCaseOrderByNameAsc(search);
    }

    @Transactional
    public Recipe updateRecipe(
            Long id,
            String name,
            boolean active
    ) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Recipe not found.")
                );

        String cleanedName = name.trim();

        recipeRepository.findByNameIgnoreCase(cleanedName)
                .filter(existingRecipe ->
                        !existingRecipe.getId().equals(id)
                )
                .ifPresent(existingRecipe -> {
                    throw new DuplicateRecipeException(
                            "A recipe with that name already exists."
                    );
                });

        recipe.setName(cleanedName);
        recipe.setActive(active);

        return recipeRepository.save(recipe);
    }

}