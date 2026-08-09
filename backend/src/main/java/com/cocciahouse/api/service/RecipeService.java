package com.cocciahouse.api.service;

import com.cocciahouse.api.model.Recipe;
import com.cocciahouse.api.repository.RecipeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import com.cocciahouse.api.exception.DuplicateRecipeException;
@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;

    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
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
}