package com.cocciahouse.api.controller.admin;

import com.cocciahouse.api.dto.recipe.RecipeRequest;
import com.cocciahouse.api.dto.recipe.RecipeResponse;
import com.cocciahouse.api.dto.recipe.UpdateRecipeRequest;
import com.cocciahouse.api.dto.weeklyOffering.WeeklyOfferingItemResponse;
import com.cocciahouse.api.model.Recipe;
import com.cocciahouse.api.service.RecipeService;
import com.cocciahouse.api.service.ImageService;
import com.cocciahouse.api.service.ImageUploadResult;

import java.io.IOException;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/admin/recipes")
public class RecipeController {

    private final RecipeService recipeService;
    private final ImageService imageService;

    public RecipeController(RecipeService recipeService, ImageService imageService) {
        this.recipeService = recipeService;
        this.imageService = imageService;
    }

    @GetMapping
    public List<RecipeResponse> getRecipes(
            @RequestParam(required = false) String search
    ) {

        List<Recipe> recipes;

        if (search == null || search.isBlank()) {
            recipes = recipeService.getAllRecipes();
        } else {
            recipes = recipeService.searchRecipes(search.trim());
        }

        return recipes.stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/active-recipes")
    public List<RecipeResponse> getActiveRecipes(
            @RequestParam(required = false) String search
    ) {

        List<Recipe> recipes;

        if (search == null || search.isBlank()) {
            recipes = recipeService.getActiveRecipes();
        } else {
            recipes = recipeService.searchActiveRecipes(
                    search.trim()
            );
        }

        return recipes.stream()
                .map(this::toResponse)
                .toList();
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
    public ResponseEntity<RecipeResponse> createRecipe(
            @Valid @RequestBody RecipeRequest request
    ) {

        Recipe recipe = recipeService.createRecipe(
                request.name(),
                request.description(),
                request.imageAlt()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(recipe));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecipeResponse> updateRecipe(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRecipeRequest request
    ) {

        Recipe recipe = recipeService.updateRecipe(
                id,
                request.name(),
                request.description(),
                request.imageAlt(),
                request.active()
        );

        return ResponseEntity.ok(toResponse(recipe));
    }

    @PostMapping("/{id}/image")
    public ResponseEntity<RecipeResponse> uploadRecipeImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) throws IOException {

        Recipe existingRecipe =
                recipeService.getRecipeById(id);

        String oldImagePublicId =
                existingRecipe.getImagePublicId();

        ImageUploadResult uploadResult =
                imageService.uploadRecipeImage(file);

        Recipe updatedRecipe;

        try {
            updatedRecipe =
                    recipeService.updateRecipeImage(
                            id,
                            uploadResult.url(),
                            uploadResult.publicId()
                    );
        } catch (RuntimeException exception) {

            try {
                imageService.deleteImage(
                        uploadResult.publicId()
                );
            } catch (IOException cleanupException) {
                exception.addSuppressed(cleanupException);
            }

            throw exception;
        }

        if (
                oldImagePublicId != null
                        && !oldImagePublicId.isBlank()
                        && !oldImagePublicId.equals(
                        uploadResult.publicId()
                )
        ) {
            try {
                imageService.deleteImage(
                        oldImagePublicId
                );
            } catch (IOException ignored) {
                // The new recipe image is already saved.
                // A failed cleanup should not make the upload appear to fail.
            }
        }

        return ResponseEntity.ok(
                toResponse(updatedRecipe)
        );
    }

    private RecipeResponse toResponse(Recipe recipe) {
        return new RecipeResponse(
                recipe.getId(),
                recipe.getName(),
                recipe.getDescription(),
                recipe.getImageUrl(),
                recipe.getImageAlt(),
                recipe.isActive()
        );
    }
}