package com.cocciahouse.api.controller.publicapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PublicRestaurantHoursControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void publicCanFetchRestaurantHours()
            throws Exception {

        mockMvc.perform(
                        get("/api/public/hours")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()")
                        .value(7))
                .andExpect(jsonPath("$[0].dayOfWeek")
                        .value(0))
                .andExpect(jsonPath("$[0].dayName")
                        .value("Sunday"))
                .andExpect(jsonPath("$[0].closed")
                        .value(false))
                .andExpect(jsonPath("$[0].openTime")
                        .value("15:00:00"))
                .andExpect(jsonPath("$[0].closeTime")
                        .value("20:00:00"))
                .andExpect(jsonPath("$[0].note")
                        .value("Carryout only on Sundays"));
    }

    @Test
    void publicHoursDoNotRequireAuthentication()
            throws Exception {

        mockMvc.perform(
                        get("/api/public/hours")
                )
                .andExpect(status().isOk());
    }
}