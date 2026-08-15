package com.cocciahouse.api.controller.publicapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cocciahouse.api.model.OfferingType;
import com.cocciahouse.api.model.Recipe;
import com.cocciahouse.api.model.WeeklyOffering;
import com.cocciahouse.api.model.WeeklyOfferingItem;
import com.cocciahouse.api.model.WeeklyOfferingItemPrice;
import com.cocciahouse.api.model.WeeklyOfferingStatus;
import com.cocciahouse.api.repository.RecipeRepository;
import com.cocciahouse.api.repository.WeeklyOfferingRepository;
import org.springframework.transaction.annotation.Transactional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.math.BigDecimal;
import java.time.LocalDate;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PublicWeeklyOfferingControllerIntegrationTest {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private WeeklyOfferingRepository weeklyOfferingRepository;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUpCurrentOffering() {

        Recipe dinnerRecipe =
                recipeRepository.save(
                        new Recipe("Test Chicken Cacciatore")
                );

        Recipe soupRecipe =
                recipeRepository.save(
                        new Recipe("Test Italian Wedding Soup")
                );

        Recipe dessertRecipe =
                recipeRepository.save(
                        new Recipe("Test Cannoli")
                );

        WeeklyOffering offering = new WeeklyOffering();

        LocalDate today = LocalDate.now();

        offering.setStartDate(today.minusDays(1));
        offering.setEndDate(today.plusDays(1));
        offering.setStatus(WeeklyOfferingStatus.PUBLISHED);


        // DINNER
        WeeklyOfferingItem dinner = new WeeklyOfferingItem();

        dinner.setRecipe(dinnerRecipe);
        dinner.setOfferingType(OfferingType.DINNER);
        dinner.setIncludesHouseSalad(true);
        dinner.setIncludesHomemadeBread(true);
        dinner.setDisplayOrder(0);

        dinner.addPrice(
                new WeeklyOfferingItemPrice(
                        null,
                        new BigDecimal("21.95"),
                        0
                )
        );

        offering.addItem(dinner);


        // SOUP
        WeeklyOfferingItem soup = new WeeklyOfferingItem();

        soup.setRecipe(soupRecipe);
        soup.setOfferingType(OfferingType.SOUP);
        soup.setDisplayOrder(1);

        soup.addPrice(
                new WeeklyOfferingItemPrice(
                        "Cup",
                        new BigDecimal("4.50"),
                        0
                )
        );

        soup.addPrice(
                new WeeklyOfferingItemPrice(
                        "Bowl",
                        new BigDecimal("6.50"),
                        1
                )
        );

        offering.addItem(soup);


        // DESSERT
        WeeklyOfferingItem dessert = new WeeklyOfferingItem();

        dessert.setRecipe(dessertRecipe);
        dessert.setOfferingType(OfferingType.DESSERT);
        dessert.setDisplayOrder(2);

        dessert.addPrice(
                new WeeklyOfferingItemPrice(
                        null,
                        new BigDecimal("5.95"),
                        0
                )
        );

        offering.addItem(dessert);


        weeklyOfferingRepository.save(offering);
    }

    @Test
    void getCurrentOfferingReturnsPublishedOffering() throws Exception {
        mockMvc.perform(
                        get("/api/public/weekly-offerings/current")
                )
                .andExpect(status().isOk())
                .andExpect(
                        content().contentTypeCompatibleWith("application/json")
                )
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items.length()").value(3))

                .andExpect(
                        jsonPath("$.items[0].offeringType")
                                .value("DINNER")
                )
                .andExpect(
                        jsonPath("$.items[0].recipeName")
                                .value("Test Chicken Cacciatore")
                )
                .andExpect(
                        jsonPath("$.items[0].includedSidesText")
                                .value(
                                        "Served with house salad and homemade bread."
                                )
                )

                .andExpect(
                        jsonPath("$.items[1].offeringType")
                                .value("SOUP")
                )
                .andExpect(
                        jsonPath("$.items[1].prices.length()")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$.items[1].prices[0].label")
                                .value("Cup")
                )
                .andExpect(
                        jsonPath("$.items[1].prices[1].label")
                                .value("Bowl")
                )

                .andExpect(
                        jsonPath("$.items[2].offeringType")
                                .value("DESSERT")
                )
                .andExpect(
                        jsonPath("$.items[2].recipeName")
                                .value("Test Cannoli")
                )
                .andExpect(jsonPath("$.status").value("PUBLISHED")
                );
    }
}