package com.cocciahouse.api.service;

import com.cocciahouse.api.model.Recipe;
import com.cocciahouse.api.repository.RecipeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    private RecipeService recipeService;

    @BeforeEach
    void setUp() {
        recipeService = new RecipeService(recipeRepository);
    }

    @Test
    void getActiveRecipes_returnsActiveRecipes() {

        Recipe bakedZiti = new Recipe("Baked Ziti");
        Recipe chickenMarsala = new Recipe("Chicken Marsala");

        when(recipeRepository.findAllByActiveTrueOrderByNameAsc())
                .thenReturn(List.of(
                        bakedZiti,
                        chickenMarsala
                ));

        List<Recipe> result = recipeService.getActiveRecipes();

        assertEquals(2, result.size());
        assertEquals("Baked Ziti", result.get(0).getName());
        assertEquals("Chicken Marsala", result.get(1).getName());

        verify(recipeRepository)
                .findAllByActiveTrueOrderByNameAsc();
    }

    @Test
    void searchActiveRecipes_returnsMatchingRecipes() {

        Recipe bakedZiti = new Recipe("Baked Ziti");

        when(
                recipeRepository
                        .findByActiveTrueAndNameContainingIgnoreCaseOrderByNameAsc("ziti")
        ).thenReturn(List.of(bakedZiti));

        List<Recipe> result =
                recipeService.searchActiveRecipes("ziti");

        assertEquals(1, result.size());
        assertEquals("Baked Ziti", result.get(0).getName());

        verify(recipeRepository)
                .findByActiveTrueAndNameContainingIgnoreCaseOrderByNameAsc("ziti");
    }

    @Test
    void createRecipe_trimsWhitespaceBeforeSaving() {

        Recipe savedRecipe = new Recipe("Pork Chop");

        when(recipeRepository.existsByNameIgnoreCase("Pork Chop"))
                .thenReturn(false);

        when(recipeRepository.save(any(Recipe.class)))
                .thenReturn(savedRecipe);

        Recipe result =
                recipeService.createRecipe("   Pork Chop   ");

        assertEquals("Pork Chop", result.getName());

        verify(recipeRepository)
                .existsByNameIgnoreCase("Pork Chop");

        verify(recipeRepository).save(argThat(recipe ->
                recipe.getName().equals("Pork Chop")
        ));
    }

    @Test
    void createRecipe_savesNewRecipe() {

        when(recipeRepository.existsByNameIgnoreCase("Lasagna"))
                .thenReturn(false);

        Recipe savedRecipe = new Recipe("Lasagna");

        when(recipeRepository.save(any(Recipe.class)))
                .thenReturn(savedRecipe);

        Recipe result =
                recipeService.createRecipe("Lasagna");

        assertEquals("Lasagna", result.getName());
        assertTrue(result.isActive());

        verify(recipeRepository)
                .existsByNameIgnoreCase("Lasagna");

        verify(recipeRepository)
                .save(any(Recipe.class));
    }

    @Test
    void createRecipe_rejectsDuplicateNameIgnoringCase() {

        when(recipeRepository.existsByNameIgnoreCase("Baked Ziti"))
                .thenReturn(true);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> recipeService.createRecipe("Baked Ziti")
                );

        assertEquals(
                "A recipe with that name already exists.",
                exception.getMessage()
        );

        verify(recipeRepository)
                .existsByNameIgnoreCase("Baked Ziti");

        verify(recipeRepository, never())
                .save(any(Recipe.class));
    }

    @Test
    void createRecipe_rejectsDuplicateNameAfterTrimmingWhitespace() {

        when(recipeRepository.existsByNameIgnoreCase("Baked Ziti"))
                .thenReturn(true);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> recipeService.createRecipe("   Baked Ziti   ")
                );

        assertEquals(
                "A recipe with that name already exists.",
                exception.getMessage()
        );

        verify(recipeRepository)
                .existsByNameIgnoreCase("Baked Ziti");

        verify(recipeRepository, never())
                .save(any(Recipe.class));
    }
}