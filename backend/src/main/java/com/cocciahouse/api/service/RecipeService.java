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
}