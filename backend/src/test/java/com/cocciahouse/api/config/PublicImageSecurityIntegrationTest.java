
package com.cocciahouse.api.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PublicImageSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void publicImageDoesNotRequireAuthentication() throws Exception {
        mockMvc.perform(get(
                        "/api/public/images/images/recipes/" +
                                "123e4567-e89b-12d3-a456-426614174000.webp"
                ))
                .andExpect(status().isNotFound());
    }

    @Test
    void adminEndpointsStillRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isUnauthorized());
    }
}
