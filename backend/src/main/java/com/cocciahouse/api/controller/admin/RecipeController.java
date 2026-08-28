package com.cocciahouse.api.controller.admin;

import com.cocciahouse.api.dto.recipe.RecipeRequest;
import com.cocciahouse.api.dto.recipe.RecipeResponse;
import com.cocciahouse.api.model.Recipe;
import com.cocciahouse.api.service.RecipeService;
import com.cocciahouse.api.dto.recipe.UpdateRecipeRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.cocciahouse.api.dto.weeklyOffering.WeeklyOfferingItemResponse;

import java.util.List;

@RestController
@RequestMapping("/api/admin/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping
    public List<RecipeResponse> getRecipes(@RequestParam(required = false) String search) {

        List<Recipe> recipes;

        if (search == null || search.isBlank()) {
            recipes = recipeService.getAllRecipes();
        } else {
            recipes = recipeService.searchRecipes(search.trim());
        }
        return recipes.stream().map(this::toResponse).toList();
    }

    @GetMapping("/{recipeId}/latest-offering-item")
    public ResponseEntity<WeeklyOfferingItemResponse> getLatestOfferingItem(
            @PathVariable Long recipeId
    ) {
        return recipeService
                .getLatestOfferingItem(recipeId)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.noContent().build()
                );
    }

    @PostMapping
    public ResponseEntity<RecipeResponse> createRecipe(@Valid @RequestBody RecipeRequest request) {

        Recipe recipe = recipeService.createRecipe(request.name());

        RecipeResponse response = toResponse(recipe);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecipeResponse> updateRecipe(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRecipeRequest request
    ) {
        Recipe recipe = recipeService.updateRecipe(
                id,
                request.name(),
                request.active()
        );

        return ResponseEntity.ok(toResponse(recipe));
    }

    private RecipeResponse toResponse(Recipe recipe) {
        return new RecipeResponse(recipe.getId(), recipe.getName(), recipe.isActive());
    }
}