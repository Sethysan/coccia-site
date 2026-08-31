package com.cocciahouse.api.service;

import com.cocciahouse.api.dto.weeklyOffering.WeeklyOfferingItemResponse;
import com.cocciahouse.api.exception.DuplicateRecipeException;
import com.cocciahouse.api.exception.RecipeNotFoundException;
import com.cocciahouse.api.mapper.WeeklyOfferingMapper;
import com.cocciahouse.api.model.Recipe;
import com.cocciahouse.api.model.WeeklyOfferingItem;
import com.cocciahouse.api.model.WeeklyOfferingStatus;
import com.cocciahouse.api.repository.RecipeRepository;
import com.cocciahouse.api.repository.WeeklyOfferingItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private WeeklyOfferingItemRepository weeklyOfferingItemRepository;

    @Mock
    private WeeklyOfferingMapper weeklyOfferingMapper;

    private RecipeService recipeService;

    @BeforeEach
    void setUp() {
        recipeService = new RecipeService(
                recipeRepository,
                weeklyOfferingItemRepository,
                weeklyOfferingMapper
        );
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
                        .findByActiveTrueAndNameContainingIgnoreCaseOrderByNameAsc(
                                "ziti"
                        )
        ).thenReturn(List.of(bakedZiti));

        List<Recipe> result =
                recipeService.searchActiveRecipes("ziti");

        assertEquals(1, result.size());
        assertEquals("Baked Ziti", result.getFirst().getName());

        verify(recipeRepository)
                .findByActiveTrueAndNameContainingIgnoreCaseOrderByNameAsc(
                        "ziti"
                );
    }

    @Test
    void createRecipe_trimsWhitespaceBeforeSaving() {

        Recipe savedRecipe = new Recipe("Pork Chop");

        when(recipeRepository.existsByNameIgnoreCase("Pork Chop"))
                .thenReturn(false);

        when(recipeRepository.save(any(Recipe.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Recipe result =
                recipeService.createRecipe(
                        "   Pork Chop   ",
                        "  Grilled pork chop.  ",
                        "  Pork chop on a plate  "
                );

        assertEquals("Pork Chop", result.getName());
        assertEquals("Grilled pork chop.", result.getDescription());
        assertEquals(
                "Pork chop on a plate",
                result.getImageAlt()
        );

        verify(recipeRepository)
                .existsByNameIgnoreCase("Pork Chop");

        verify(recipeRepository).save(argThat(recipe ->
                recipe.getName().equals("Pork Chop")
                        && recipe.getDescription()
                        .equals("Grilled pork chop.")
                        && recipe.getImageAlt()
                        .equals("Pork chop on a plate")
        ));
    }

    @Test
    void createRecipe_convertsBlankOptionalTextToNull() {

        when(recipeRepository.existsByNameIgnoreCase("Lasagna"))
                .thenReturn(false);

        when(recipeRepository.save(any(Recipe.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Recipe result =
                recipeService.createRecipe(
                        "Lasagna",
                        "   ",
                        "   "
                );

        assertNull(result.getDescription());
        assertNull(result.getImageAlt());
    }

    @Test
    void createRecipe_savesNewRecipe() {

        when(recipeRepository.existsByNameIgnoreCase("Lasagna"))
                .thenReturn(false);

        when(recipeRepository.save(any(Recipe.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Recipe result =
                recipeService.createRecipe(
                        "Lasagna",
                        "Layers of pasta, sauce, and cheese.",
                        "Slice of lasagna"
                );

        assertEquals("Lasagna", result.getName());
        assertEquals(
                "Layers of pasta, sauce, and cheese.",
                result.getDescription()
        );
        assertEquals(
                "Slice of lasagna",
                result.getImageAlt()
        );
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

        DuplicateRecipeException exception =
                assertThrows(
                        DuplicateRecipeException.class,
                        () -> recipeService.createRecipe(
                                "Baked Ziti",
                                null,
                                null
                        )
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

        DuplicateRecipeException exception =
                assertThrows(
                        DuplicateRecipeException.class,
                        () -> recipeService.createRecipe(
                                "   Baked Ziti   ",
                                null,
                                null
                        )
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
    void getLatestOfferingItem_returnsMappedLatestPublishedOrScheduledItem() {

        Long recipeId = 1L;

        WeeklyOfferingItem item = new WeeklyOfferingItem();

        WeeklyOfferingItemResponse response =
                mock(WeeklyOfferingItemResponse.class);

        when(
                weeklyOfferingItemRepository
                        .findFirstByRecipeIdAndWeeklyOfferingStatusInOrderByWeeklyOfferingStartDateDesc(
                                eq(recipeId),
                                anyCollection()
                        )
        ).thenReturn(Optional.of(item));

        when(weeklyOfferingMapper.toItemResponse(item))
                .thenReturn(response);

        when(recipeRepository.existsById(recipeId))
                .thenReturn(true);

        Optional<WeeklyOfferingItemResponse> result =
                recipeService.getLatestOfferingItem(recipeId);

        assertTrue(result.isPresent());
        assertSame(response, result.get());

        verify(weeklyOfferingItemRepository)
                .findFirstByRecipeIdAndWeeklyOfferingStatusInOrderByWeeklyOfferingStartDateDesc(
                        eq(recipeId),
                        argThat(statuses ->
                                statuses.contains(
                                        WeeklyOfferingStatus.PUBLISHED
                                )
                                        && statuses.contains(
                                        WeeklyOfferingStatus.SCHEDULED
                                )
                                        && !statuses.contains(
                                        WeeklyOfferingStatus.DRAFT
                                )
                        )
                );

        verify(weeklyOfferingMapper)
                .toItemResponse(item);
    }

    @Test
    void getLatestOfferingItem_returnsEmptyWhenRecipeHasNoPreviousOffering() {

        Long recipeId = 1L;

        when(
                weeklyOfferingItemRepository
                        .findFirstByRecipeIdAndWeeklyOfferingStatusInOrderByWeeklyOfferingStartDateDesc(
                                eq(recipeId),
                                anyCollection()
                        )
        ).thenReturn(Optional.empty());

        when(recipeRepository.existsById(recipeId))
                .thenReturn(true);

        Optional<WeeklyOfferingItemResponse> result =
                recipeService.getLatestOfferingItem(recipeId);

        assertTrue(result.isEmpty());

        verify(weeklyOfferingMapper, never())
                .toItemResponse(any());
    }

    @Test
    void updateRecipeUpdatesRecipeContentAndActiveStatus() {

        Recipe recipe = new Recipe("Chicken Parmesan");

        when(recipeRepository.findById(1L))
                .thenReturn(Optional.of(recipe));

        when(recipeRepository.findByNameIgnoreCase("Chicken Parm"))
                .thenReturn(Optional.empty());

        when(recipeRepository.save(recipe))
                .thenReturn(recipe);

        Recipe updatedRecipe =
                recipeService.updateRecipe(
                        1L,
                        "Chicken Parm",
                        "  Breaded chicken with sauce and cheese.  ",
                        "  Chicken parmesan on a plate  ",
                        false
                );

        assertEquals(
                "Chicken Parm",
                updatedRecipe.getName()
        );

        assertEquals(
                "Breaded chicken with sauce and cheese.",
                updatedRecipe.getDescription()
        );

        assertEquals(
                "Chicken parmesan on a plate",
                updatedRecipe.getImageAlt()
        );

        assertFalse(updatedRecipe.isActive());

        verify(recipeRepository).save(recipe);
    }

    @Test
    void updateRecipe_convertsBlankOptionalTextToNull() {

        Recipe recipe = mock(Recipe.class);

        when(recipe.getId())
                .thenReturn(1L);

        when(recipeRepository.findById(1L))
                .thenReturn(Optional.of(recipe));

        when(recipeRepository.findByNameIgnoreCase("Chicken Parmesan"))
                .thenReturn(Optional.of(recipe));

        when(recipeRepository.save(recipe))
                .thenReturn(recipe);

        Recipe updatedRecipe =
                recipeService.updateRecipe(
                        1L,
                        "Chicken Parmesan",
                        "   ",
                        "   ",
                        true
                );

        verify(recipe).setName("Chicken Parmesan");
        verify(recipe).setDescription(null);
        verify(recipe).setImageAlt(null);
        verify(recipe).setActive(true);
        verify(recipeRepository).save(recipe);

        assertSame(recipe, updatedRecipe);
    }

    @Test
    void updateRecipeRejectsDuplicateNameFromAnotherRecipe() {

        Recipe recipe = mock(Recipe.class);
        Recipe existingRecipe = mock(Recipe.class);

        when(existingRecipe.getId())
                .thenReturn(2L);

        when(recipeRepository.findById(1L))
                .thenReturn(Optional.of(recipe));

        when(recipeRepository.findByNameIgnoreCase("Chicken Parm"))
                .thenReturn(Optional.of(existingRecipe));

        assertThrows(
                DuplicateRecipeException.class,
                () -> recipeService.updateRecipe(
                        1L,
                        "Chicken Parm",
                        null,
                        null,
                        true
                )
        );

        verify(recipeRepository, never())
                .save(any());
    }

    @Test
    void updateRecipeImage_savesImageUrlAndPublicId() {

        Recipe recipe = new Recipe("Pork Chop");

        when(recipeRepository.findById(1L))
                .thenReturn(Optional.of(recipe));

        when(recipeRepository.save(recipe))
                .thenReturn(recipe);

        Recipe updatedRecipe =
                recipeService.updateRecipeImage(
                        1L,
                        "https://example.com/pork-chop.jpg",
                        "coccia-house/recipes/pork-chop"
                );

        assertEquals(
                "https://example.com/pork-chop.jpg",
                updatedRecipe.getImageUrl()
        );

        assertEquals(
                "coccia-house/recipes/pork-chop",
                updatedRecipe.getImagePublicId()
        );

        verify(recipeRepository).save(recipe);
    }

    @Test
    void getLatestOfferingItem_throwsWhenRecipeDoesNotExist() {

        Long recipeId = 999L;

        when(recipeRepository.existsById(recipeId))
                .thenReturn(false);

        RecipeNotFoundException exception =
                assertThrows(
                        RecipeNotFoundException.class,
                        () -> recipeService.getLatestOfferingItem(recipeId)
                );

        assertEquals(
                "Recipe not found.",
                exception.getMessage()
        );
    }

}