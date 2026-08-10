package com.cocciahouse.api.controller.admin;


import com.cocciahouse.api.model.Recipe;
import com.cocciahouse.api.service.RecipeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.cocciahouse.api.exception.DuplicateRecipeException;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecipeController.class)
@ActiveProfiles("test")
class RecipeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RecipeService recipeService;


    @Test
    void getRecipes_returnsAllActiveRecipes() throws Exception {

        Recipe bakedZiti = new Recipe("Baked Ziti");
        Recipe chickenMarsala = new Recipe("Chicken Marsala");

        when(recipeService.getActiveRecipes()).thenReturn(List.of(bakedZiti, chickenMarsala));

        mockMvc.perform(get("/api/admin/recipes")).andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith("application/json")).andExpect(jsonPath("$[0].name").value("Baked Ziti")).andExpect(jsonPath("$[0].active").value(true)).andExpect(jsonPath("$[1].name").value("Chicken Marsala")).andExpect(jsonPath("$[1].active").value(true));

        verify(recipeService).getActiveRecipes();
    }


    @Test
    void getRecipes_withSearch_returnsMatchingRecipes() throws Exception {

        Recipe bakedZiti = new Recipe("Baked Ziti");

        when(recipeService.searchActiveRecipes("ziti")).thenReturn(List.of(bakedZiti));

        mockMvc.perform(get("/api/admin/recipes").param("search", "ziti")).andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith("application/json")).andExpect(jsonPath("$.length()").value(1)).andExpect(jsonPath("$[0].name").value("Baked Ziti")).andExpect(jsonPath("$[0].active").value(true));

        verify(recipeService).searchActiveRecipes("ziti");
    }


    @Test
    void createRecipe_returns201AndCreatedRecipe() throws Exception {

        Recipe porkChop = new Recipe("Pork Chop");

        when(recipeService.createRecipe("Pork Chop")).thenReturn(porkChop);

        mockMvc.perform(post("/api/admin/recipes").contentType("application/json").content("""
                {
                  "name": "Pork Chop"
                }
                """)).andExpect(status().isCreated()).andExpect(content().contentTypeCompatibleWith("application/json")).andExpect(jsonPath("$.name").value("Pork Chop")).andExpect(jsonPath("$.active").value(true));

        verify(recipeService).createRecipe("Pork Chop");
    }

    @Test
    void createRecipe_withBlankName_returns400() throws Exception {

        mockMvc.perform(post("/api/admin/recipes").contentType("application/json").content("""
                {
                  "name": ""
                }
                """)).andExpect(status().isBadRequest());
    }


    @Test
    void createRecipe_withMissingName_returns400() throws Exception {

        mockMvc.perform(post("/api/admin/recipes").contentType("application/json").content("""
                {
                }
                """)).andExpect(status().isBadRequest());
    }

    @Test
    void createRecipe_whenDuplicate_returns409Conflict() throws Exception {

        when(recipeService.createRecipe("Baked Ziti"))
                .thenThrow(
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

        verify(recipeService).createRecipe("Baked Ziti");
    }


}