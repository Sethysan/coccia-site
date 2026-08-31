package com.cocciahouse.api.controller.admin;

import com.cocciahouse.api.dto.recipe.OfferingItemPriceResponse;
import com.cocciahouse.api.dto.weeklyOffering.WeeklyOfferingItemResponse;
import com.cocciahouse.api.exception.DuplicateRecipeException;
import com.cocciahouse.api.model.OfferingType;
import com.cocciahouse.api.model.Recipe;
import com.cocciahouse.api.service.RecipeService;
import com.cocciahouse.api.service.ImageService;
import com.cocciahouse.api.service.ImageUploadResult;
import org.springframework.mock.web.MockMultipartFile;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecipeController.class)
@ActiveProfiles("test")
class RecipeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RecipeService recipeService;

    @MockitoBean
    private ImageService imageService;

    @Test
    void getRecipes_returnsAllRecipes() throws Exception {

        Recipe bakedZiti = new Recipe("Baked Ziti");
        bakedZiti.setDescription("Baked pasta with cheese.");
        bakedZiti.setImageAlt("Baked ziti in a casserole dish");

        Recipe chickenMarsala = new Recipe("Chicken Marsala");
        chickenMarsala.setActive(false);

        when(recipeService.getAllRecipes())
                .thenReturn(List.of(bakedZiti, chickenMarsala));

        mockMvc.perform(
                        get("/api/admin/recipes")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$[0].name").value("Baked Ziti"))
                .andExpect(jsonPath("$[0].description")
                        .value("Baked pasta with cheese."))
                .andExpect(jsonPath("$[0].imageAlt")
                        .value("Baked ziti in a casserole dish"))
                .andExpect(jsonPath("$[0].active").value(true))
                .andExpect(jsonPath("$[1].name").value("Chicken Marsala"))
                .andExpect(jsonPath("$[1].active").value(false));

        verify(recipeService).getAllRecipes();
    }

    @Test
    void getRecipes_withSearch_returnsMatchingRecipes() throws Exception {

        Recipe bakedZiti = new Recipe("Baked Ziti");

        when(recipeService.searchRecipes("ziti"))
                .thenReturn(List.of(bakedZiti));

        mockMvc.perform(
                        get("/api/admin/recipes")
                                .param("search", "ziti")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Baked Ziti"))
                .andExpect(jsonPath("$[0].active").value(true));

        verify(recipeService).searchRecipes("ziti");
    }

    @Test
    void createRecipe_returns201AndCreatedRecipe() throws Exception {

        Recipe porkChop = new Recipe("Pork Chop");
        porkChop.setDescription("Grilled pork chop.");
        porkChop.setImageAlt("Grilled pork chop on a dinner plate");

        when(
                recipeService.createRecipe(
                        "Pork Chop",
                        "Grilled pork chop.",
                        "Grilled pork chop on a dinner plate"
                )
        ).thenReturn(porkChop);

        mockMvc.perform(
                        post("/api/admin/recipes")
                                .contentType("application/json")
                                .content("""
                                        {
                                          "name": "Pork Chop",
                                          "description": "Grilled pork chop.",
                                          "imageAlt": "Grilled pork chop on a dinner plate"
                                        }
                                        """)
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.name").value("Pork Chop"))
                .andExpect(jsonPath("$.description")
                        .value("Grilled pork chop."))
                .andExpect(jsonPath("$.imageAlt")
                        .value("Grilled pork chop on a dinner plate"))
                .andExpect(jsonPath("$.active").value(true));

        verify(recipeService).createRecipe(
                "Pork Chop",
                "Grilled pork chop.",
                "Grilled pork chop on a dinner plate"
        );
    }

    @Test
    void createRecipe_withBlankName_returns400() throws Exception {

        mockMvc.perform(
                        post("/api/admin/recipes")
                                .contentType("application/json")
                                .content("""
                                        {
                                          "name": ""
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void createRecipe_withMissingName_returns400() throws Exception {

        mockMvc.perform(
                        post("/api/admin/recipes")
                                .contentType("application/json")
                                .content("""
                                        {
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void createRecipe_whenDuplicate_returns409Conflict() throws Exception {

        when(
                recipeService.createRecipe(
                        "Baked Ziti",
                        null,
                        null
                )
        ).thenThrow(
                new DuplicateRecipeException(
                        "A recipe with that name already exists."
                )
        );

        mockMvc.perform(
                        post("/api/admin/recipes")
                                .contentType("application/json")
                                .content("""
                                        {
                                          "name": "Baked Ziti"
                                        }
                                        """)
                )
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message")
                        .value("A recipe with that name already exists."));

        verify(recipeService).createRecipe(
                "Baked Ziti",
                null,
                null
        );
    }

    @Test
    void getLatestOfferingItem_returnsPreviousUsage() throws Exception {

        WeeklyOfferingItemResponse response =
                new WeeklyOfferingItemResponse(
                        10L,
                        1L,
                        "Chicken Cacciatore",
                        OfferingType.DINNER,
                        "Slow-cooked chicken.",
                        null,
                        null,
                        true,
                        true,
                        "Served with house salad and homemade bread.",
                        0,
                        List.of(
                                new OfferingItemPriceResponse(
                                        20L,
                                        null,
                                        new BigDecimal("21.95"),
                                        0
                                )
                        )
                );

        when(recipeService.getLatestOfferingItem(1L))
                .thenReturn(Optional.of(response));

        mockMvc.perform(
                        get(
                                "/api/admin/recipes/{recipeId}/latest-offering-item",
                                1L
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recipeId").value(1))
                .andExpect(jsonPath("$.recipeName")
                        .value("Chicken Cacciatore"))
                .andExpect(jsonPath("$.offeringType").value("DINNER"))
                .andExpect(jsonPath("$.prices.length()").value(1))
                .andExpect(jsonPath("$.prices[0].amount").value(21.95));

        verify(recipeService).getLatestOfferingItem(1L);
    }

    @Test
    void getLatestOfferingItem_whenNoPreviousUsage_returns204()
            throws Exception {

        when(recipeService.getLatestOfferingItem(1L))
                .thenReturn(Optional.empty());

        mockMvc.perform(
                        get(
                                "/api/admin/recipes/{recipeId}/latest-offering-item",
                                1L
                        )
                )
                .andExpect(status().isNoContent());

        verify(recipeService).getLatestOfferingItem(1L);
    }

    @Test
    void updateRecipe_withBlankName_returns400() throws Exception {

        mockMvc.perform(
                        put("/api/admin/recipes/{id}", 1L)
                                .contentType("application/json")
                                .content("""
                                        {
                                          "name": "",
                                          "description": null,
                                          "imageAlt": null,
                                          "active": true
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateRecipe_withMissingActiveStatus_returns400()
            throws Exception {

        mockMvc.perform(
                        put("/api/admin/recipes/{id}", 1L)
                                .contentType("application/json")
                                .content("""
                                        {
                                          "name": "Chicken Parm",
                                          "description": "Breaded chicken.",
                                          "imageAlt": "Chicken parmesan"
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateRecipe_returns200AndUpdatedRecipe() throws Exception {

        Recipe updatedRecipe = new Recipe("Chicken Parm");
        updatedRecipe.setDescription(
                "Breaded chicken with sauce and cheese."
        );
        updatedRecipe.setImageAlt(
                "Chicken parmesan covered in melted cheese"
        );
        updatedRecipe.setActive(false);

        when(
                recipeService.updateRecipe(
                        1L,
                        "Chicken Parm",
                        "Breaded chicken with sauce and cheese.",
                        "Chicken parmesan covered in melted cheese",
                        false
                )
        ).thenReturn(updatedRecipe);

        mockMvc.perform(
                        put("/api/admin/recipes/{id}", 1L)
                                .contentType("application/json")
                                .content("""
                                        {
                                          "name": "Chicken Parm",
                                          "description": "Breaded chicken with sauce and cheese.",
                                          "imageAlt": "Chicken parmesan covered in melted cheese",
                                          "active": false
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.name").value("Chicken Parm"))
                .andExpect(jsonPath("$.description")
                        .value("Breaded chicken with sauce and cheese."))
                .andExpect(jsonPath("$.imageAlt")
                        .value("Chicken parmesan covered in melted cheese"))
                .andExpect(jsonPath("$.active").value(false));

        verify(recipeService).updateRecipe(
                1L,
                "Chicken Parm",
                "Breaded chicken with sauce and cheese.",
                "Chicken parmesan covered in melted cheese",
                false
        );
    }

    @Test
    void updateRecipe_whenDuplicate_returns409Conflict()
            throws Exception {

        when(
                recipeService.updateRecipe(
                        1L,
                        "Baked Ziti",
                        null,
                        null,
                        true
                )
        ).thenThrow(
                new DuplicateRecipeException(
                        "A recipe with that name already exists."
                )
        );

        mockMvc.perform(
                        put("/api/admin/recipes/{id}", 1L)
                                .contentType("application/json")
                                .content("""
                                        {
                                          "name": "Baked Ziti",
                                          "description": null,
                                          "imageAlt": null,
                                          "active": true
                                        }
                                        """)
                )
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message")
                        .value("A recipe with that name already exists."));

        verify(recipeService).updateRecipe(
                1L,
                "Baked Ziti",
                null,
                null,
                true
        );
    }

    @Test
    void uploadRecipeImage_returnsUpdatedRecipe() throws Exception {

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "pork-chop.jpg",
                        "image/jpeg",
                        "image-data".getBytes()
                );

        ImageUploadResult uploadResult =
                new ImageUploadResult(
                        "https://example.com/pork-chop.jpg",
                        "coccia-house/recipes/pork-chop"
                );

        Recipe existingRecipe =
                new Recipe("Pork Chop");

        Recipe updatedRecipe =
                new Recipe("Pork Chop");

        updatedRecipe.setImageUrl(
                "https://example.com/pork-chop.jpg"
        );

        updatedRecipe.setImagePublicId(
                "coccia-house/recipes/pork-chop"
        );

        when(recipeService.getRecipeById(1L))
                .thenReturn(existingRecipe);

        when(imageService.uploadRecipeImage(file))
                .thenReturn(uploadResult);

        when(
                recipeService.updateRecipeImage(
                        1L,
                        "https://example.com/pork-chop.jpg",
                        "coccia-house/recipes/pork-chop"
                )
        ).thenReturn(updatedRecipe);

        mockMvc.perform(
                        multipart(
                                "/api/admin/recipes/{id}/image",
                                1L
                        ).file(file)
                )
                .andExpect(status().isOk())
                .andExpect(
                        content()
                                .contentTypeCompatibleWith(
                                        "application/json"
                                )
                )
                .andExpect(
                        jsonPath("$.name")
                                .value("Pork Chop")
                )
                .andExpect(
                        jsonPath("$.imageUrl")
                                .value(
                                        "https://example.com/pork-chop.jpg"
                                )
                )
                .andExpect(
                        jsonPath("$.active")
                                .value(true)
                );

        verify(recipeService)
                .getRecipeById(1L);

        verify(imageService)
                .uploadRecipeImage(file);

        verify(recipeService)
                .updateRecipeImage(
                        1L,
                        "https://example.com/pork-chop.jpg",
                        "coccia-house/recipes/pork-chop"
                );
    }

    @Test
    void uploadRecipeImage_replacesExistingImageAndDeletesOldImage()
            throws Exception {

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "new-pork-chop.jpg",
                        "image/jpeg",
                        "new-image-data".getBytes()
                );

        Recipe existingRecipe =
                new Recipe("Pork Chop");

        existingRecipe.setImageUrl(
                "https://example.com/old.jpg"
        );

        existingRecipe.setImagePublicId(
                "coccia-house/recipes/old-pork-chop"
        );

        ImageUploadResult uploadResult =
                new ImageUploadResult(
                        "https://example.com/new.jpg",
                        "coccia-house/recipes/new-pork-chop"
                );

        Recipe updatedRecipe =
                new Recipe("Pork Chop");

        updatedRecipe.setImageUrl(
                "https://example.com/new.jpg"
        );

        when(recipeService.getRecipeById(1L))
                .thenReturn(existingRecipe);

        when(imageService.uploadRecipeImage(file))
                .thenReturn(uploadResult);

        when(
                recipeService.updateRecipeImage(
                        1L,
                        "https://example.com/new.jpg",
                        "coccia-house/recipes/new-pork-chop"
                )
        ).thenReturn(updatedRecipe);

        mockMvc.perform(
                        multipart(
                                "/api/admin/recipes/{id}/image",
                                1L
                        ).file(file)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.imageUrl")
                                .value(
                                        "https://example.com/new.jpg"
                                )
                );

        verify(imageService)
                .deleteImage(
                        "coccia-house/recipes/old-pork-chop"
                );
    }

}