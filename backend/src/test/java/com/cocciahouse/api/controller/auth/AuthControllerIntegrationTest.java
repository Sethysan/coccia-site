package com.cocciahouse.api.controller.auth;

import com.cocciahouse.api.model.AdminUserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.mock.web.MockHttpSession;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.cocciahouse.api.model.AdminUser;
import com.cocciahouse.api.repository.AdminUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String TEST_USERNAME = "testadmin";
    private static final String TEST_PASSWORD = "testpassword";
    private static final String TEST_DISPLAY_NAME = "Test Admin";
    private static final AdminUserRole TEST_ROLE = AdminUserRole.ADMIN;


    @BeforeEach
    void setUpTestAdmin() {

        adminUserRepository
                .findByUsernameIgnoreCase(TEST_USERNAME)
                .ifPresent(adminUserRepository::delete);

        AdminUser testAdmin = new AdminUser(
                TEST_USERNAME,
                passwordEncoder.encode(TEST_PASSWORD),
                TEST_DISPLAY_NAME,
                TEST_ROLE
        );

        adminUserRepository.save(testAdmin);
    }

    @AfterEach
    void cleanUpTestAdmin() {
        adminUserRepository
                .findByUsernameIgnoreCase(TEST_USERNAME)
                .ifPresent(adminUserRepository::delete);
    }

    @Test
    void sessionReturnsUnauthenticatedWhenNoSessionExists() throws Exception {

        mockMvc.perform(
                        get("/api/auth/session")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.authenticated")
                                .value(false)
                )
                .andExpect(
                        jsonPath("$.username")
                                .doesNotExist()
                );
    }

    @Test
    void loginCreatesAuthenticatedSession() throws Exception {

        String loginJson = """
                {
                    "username": "%s",
                    "password": "%s"
                }
                """.formatted(
                TEST_USERNAME,
                TEST_PASSWORD
        );

        MockHttpSession session =
                (MockHttpSession) mockMvc.perform(
                                post("/api/auth/login")
                                        .contentType("application/json")
                                        .content(loginJson)
                        )
                        .andExpect(status().isOk())
                        .andExpect(
                                jsonPath("$.authenticated")
                                        .value(true)
                        )
                        .andExpect(
                                jsonPath("$.username")
                                        .value(TEST_USERNAME)
                        )
                        .andReturn()
                        .getRequest()
                        .getSession(false);

        mockMvc.perform(
                        get("/api/auth/session")
                                .session(session)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.authenticated")
                                .value(true)
                )
                .andExpect(
                        jsonPath("$.username")
                                .value(TEST_USERNAME)
                );
    }

    @Test
    void logoutInvalidatesAuthenticatedSession() throws Exception {

        String loginJson = """
                {
                    "username": "%s",
                    "password": "%s"
                }
                """.formatted(
                TEST_USERNAME,
                TEST_PASSWORD
        );

        MockHttpSession session =
                (MockHttpSession) mockMvc.perform(
                                post("/api/auth/login")
                                        .contentType("application/json")
                                        .content(loginJson)
                        )
                        .andExpect(status().isOk())
                        .andReturn()
                        .getRequest()
                        .getSession(false);

        mockMvc.perform(
                        post("/api/auth/logout")
                                .session(session)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.authenticated")
                                .value(false)
                );

        mockMvc.perform(
                        get("/api/admin/recipes")
                                .session(session)
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginWithInvalidPasswordReturnsUnauthorized() throws Exception {

        String loginJson = """
                {
                    "username": "%s",
                    "password": "wrongpassword"
                }
                """.formatted(TEST_USERNAME);

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType("application/json")
                                .content(loginJson)
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void authenticatedSessionCanAccessProtectedRecipes() throws Exception {

        String loginJson = """
                {
                    "username": "%s",
                    "password": "%s"
                }
                """.formatted(
                TEST_USERNAME,
                TEST_PASSWORD
        );

        MockHttpSession session =
                (MockHttpSession) mockMvc.perform(
                                post("/api/auth/login")
                                        .contentType("application/json")
                                        .content(loginJson)
                        )
                        .andExpect(status().isOk())
                        .andReturn()
                        .getRequest()
                        .getSession(false);

        mockMvc.perform(
                        get("/api/admin/recipes")
                                .session(session)
                )
                .andExpect(status().isOk());
    }

}