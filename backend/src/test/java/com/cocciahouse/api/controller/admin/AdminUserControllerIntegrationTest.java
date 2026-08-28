package com.cocciahouse.api.controller.admin;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AdminUserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void adminCanAccessUsersEndpoint() throws Exception {
        mockMvc.perform(
                        get("/api/admin/users")
                                .with(user("admin").roles("ADMIN"))
                )
                .andExpect(status().isOk());
    }

    @Test
    void staffCannotAccessUsersEndpoint() throws Exception {
        mockMvc.perform(
                        get("/api/admin/users")
                                .with(user("staff").roles("STAFF"))
                )
                .andExpect(status().isForbidden());
    }
}