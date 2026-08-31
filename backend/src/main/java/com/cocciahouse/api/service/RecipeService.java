package com.cocciahouse.api.service;

import com.cocciahouse.api.dto.weeklyOffering.WeeklyOfferingItemResponse;
import com.cocciahouse.api.exception.DuplicateRecipeException;
import com.cocciahouse.api.mapper.WeeklyOfferingMapper;
import com.cocciahouse.api.model.Recipe;
import com.cocciahouse.api.model.WeeklyOfferingStatus;
import com.cocciahouse.api.repository.RecipeRepository;
import com.cocciahouse.api.repository.WeeklyOfferingItemRepository;
import com.cocciahouse.api.exception.RecipeNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
        return recipeRepository
                .findByActiveTrueAndNameContainingIgnoreCaseOrderByNameAsc(search);
    }

    public Optional<Recipe> getByName(String name) {
        return recipeRepository.findByNameIgnoreCase(name);
    }

    @Transactional
    public Recipe createRecipe(
            String name,
            String description,
            String imageAlt
    ) {

        String cleanedName = name.trim();

        if (recipeRepository.existsByNameIgnoreCase(cleanedName)) {
            throw new DuplicateRecipeException(
                    "A recipe with that name already exists."
            );
        }

        Recipe recipe = new Recipe(cleanedName);

        recipe.setDescription(cleanNullableText(description));
        recipe.setImageAlt(cleanNullableText(imageAlt));

        return recipeRepository.save(recipe);
    }

    @Transactional(readOnly = true)
    public Optional<WeeklyOfferingItemResponse> getLatestOfferingItem(
            Long recipeId
    ) {

        if (!recipeRepository.existsById(recipeId)) {
            throw new RecipeNotFoundException(
                    "Recipe not found."
            );
        }

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
        return recipeRepository
                .findByNameContainingIgnoreCaseOrderByNameAsc(search);
    }

    @Transactional
    public Recipe updateRecipe(
            Long id,
            String name,
            String description,
            String imageAlt,
            boolean active
    ) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() ->
                        new RecipeNotFoundException("Recipe not found.")
                );

        String cleanedName = name.trim();

        recipeRepository.findByNameIgnoreCase(cleanedName)
                .filter(existingRecipe ->
                        !id.equals(existingRecipe.getId())
                )
                .ifPresent(existingRecipe -> {
                    throw new DuplicateRecipeException(
                            "A recipe with that name already exists."
                    );
                });

        recipe.setName(cleanedName);
        recipe.setDescription(cleanNullableText(description));
        recipe.setImageAlt(cleanNullableText(imageAlt));
        recipe.setActive(active);

        return recipeRepository.save(recipe);
    }

    @Transactional(readOnly = true)
    public Recipe getRecipeById(Long id) {
        return recipeRepository.findById(id)
                .orElseThrow(() ->
                        new RecipeNotFoundException("Recipe not found.")
                );
    }

    @Transactional
    public Recipe updateRecipeImage(
            Long id,
            String imageUrl,
            String imagePublicId
    ) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() ->
                        new RecipeNotFoundException("Recipe not found.")
                );

        recipe.setImageUrl(imageUrl);
        recipe.setImagePublicId(imagePublicId);

        return recipeRepository.save(recipe);
    }

    private String cleanNullableText(String value) {

        if (value == null) {
            return null;
        }

        String cleaned = value.trim();

        return cleaned.isEmpty()
                ? null
                : cleaned;
    }
}