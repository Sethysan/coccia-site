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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PublicWeeklyOfferingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

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
                                .value("Chicken Cacciatore")
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
                                .value("Cannoli")
                );
    }
}