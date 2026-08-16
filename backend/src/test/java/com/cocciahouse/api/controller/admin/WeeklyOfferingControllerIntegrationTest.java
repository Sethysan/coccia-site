package com.cocciahouse.api.controller.admin;

import com.cocciahouse.api.model.*;
import com.cocciahouse.api.repository.WeeklyOfferingItemRepository;
import com.cocciahouse.api.repository.WeeklyOfferingRepository;
import com.cocciahouse.api.repository.AdminUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.cocciahouse.api.repository.RecipeRepository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class WeeklyOfferingControllerIntegrationTest {

    @Autowired
    private WeeklyOfferingRepository weeklyOfferingRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private WeeklyOfferingItemRepository weeklyOfferingItemRepository;

    private static final String TEST_USERNAME = "testadmin";
    private static final String TEST_PASSWORD = "testpassword";

    @BeforeEach
    void setUpTestAdmin() {

        adminUserRepository
                .findByUsernameIgnoreCase(TEST_USERNAME)
                .ifPresent(adminUserRepository::delete);

        AdminUser testAdmin = new AdminUser(
                TEST_USERNAME,
                passwordEncoder.encode(TEST_PASSWORD)
        );

        adminUserRepository.save(testAdmin);
    }

    @Test
    void createOfferingWithoutAuthenticationReturnsUnauthorized()
            throws Exception {

        String requestJson = """
                {
                    "startDate": "2026-08-19",
                    "endDate": "2026-08-25"
                }
                """;

        mockMvc.perform(
                        post("/api/admin/weekly-offerings")
                                .with(csrf())
                                .contentType("application/json")
                                .content(requestJson)
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void authenticatedAdminCanCreateDraftOffering()
            throws Exception {

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

        String requestJson = """
                {
                    "startDate": "2026-08-19",
                    "endDate": "2026-08-25"
                }
                """;

        assert session != null;
        mockMvc.perform(
                        post("/api/admin/weekly-offerings")
                                .session(session)
                                .with(csrf())
                                .contentType("application/json")
                                .content(requestJson)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(
                        jsonPath("$.startDate")
                                .value("2026-08-19")
                )
                .andExpect(
                        jsonPath("$.endDate")
                                .value("2026-08-25")
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("DRAFT")
                ).andReturn();
    }

    @Test
    void createOfferingWithEndDateBeforeStartDateReturnsBadRequest()
            throws Exception {

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

        String requestJson = """
                {
                    "startDate": "2026-08-25",
                    "endDate": "2026-08-19"
                }
                """;

        assert session != null;
        mockMvc.perform(
                        post("/api/admin/weekly-offerings")
                                .session(session)
                                .with(csrf())
                                .contentType("application/json")
                                .content(requestJson)
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.status")
                                .value(400)
                )
                .andExpect(
                        jsonPath("$.error")
                                .value("Bad Request")
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "End date cannot be before start date."
                                )
                )
                .andExpect(
                        jsonPath("$.timestamp")
                                .exists()
                );
    }

    @Test
    void authenticatedAdminCanAddDinnerItemToDraftOffering()
            throws Exception {

        // Create recipe needed by the offering item
        Recipe recipe =
                recipeRepository.save(
                        new Recipe("Test Chicken Parmesan")
                );

        // Create the draft offering
        WeeklyOffering offering = new WeeklyOffering();
        offering.setStartDate(LocalDate.of(2026, 8, 19));
        offering.setEndDate(LocalDate.of(2026, 8, 25));
        offering.setStatus(WeeklyOfferingStatus.DRAFT);

        offering = weeklyOfferingRepository.save(offering);

        // Log in
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

        // Add a dinner item
        String requestJson = """
                {
                    "recipeId": %d,
                    "offeringType": "DINNER",
                    "publicDescription": "Breaded chicken with sauce and cheese.",
                    "imageUrl": null,
                    "imageAlt": null,
                    "includesHouseSalad": true,
                    "includesHomemadeBread": true,
                    "displayOrder": 0,
                    "prices": [
                        {
                            "label": null,
                            "amount": 21.95,
                            "displayOrder": 0
                        }
                    ]
                }
                """.formatted(recipe.getId());

        assert session != null;
        mockMvc.perform(
                        post(
                                "/api/admin/weekly-offerings/{offeringId}/items",
                                offering.getId()
                        )
                                .session(session)
                                .with(csrf())
                                .contentType("application/json")
                                .content(requestJson)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(
                        jsonPath("$.items[0].offeringType")
                                .value("DINNER")
                )
                .andExpect(
                        jsonPath("$.items[0].includedSidesText")
                                .value(
                                        "Served with house salad and homemade bread."
                                )
                )
                .andExpect(
                        jsonPath("$.items[0].prices.length()")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.items[0].prices[0].amount")
                                .value(21.95)
                );
    }

    @Test
    void addItemToNonexistentOfferingReturnsBadRequest()
            throws Exception {

        MockHttpSession session = loginAsTestAdmin();

        String requestJson = """
                {
                    "recipeId": 999999,
                    "offeringType": "DINNER",
                    "includesHouseSalad": true,
                    "includesHomemadeBread": true,
                    "displayOrder": 0,
                    "prices": [
                        {
                            "amount": 20.00,
                            "displayOrder": 0
                        }
                    ]
                }
                """;

        mockMvc.perform(
                        post("/api/admin/weekly-offerings/{offeringId}/items", 999999)
                                .session(session)
                                .with(csrf())
                                .contentType("application/json")
                                .content(requestJson)
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.message")
                                .value("Weekly offering not found.")
                );
    }

    @Test
    void addItemWithNonexistentRecipeReturnsBadRequest()
            throws Exception {

        WeeklyOffering offering = new WeeklyOffering();
        offering.setStartDate(LocalDate.of(2026, 8, 19));
        offering.setEndDate(LocalDate.of(2026, 8, 25));
        offering.setStatus(WeeklyOfferingStatus.DRAFT);

        offering = weeklyOfferingRepository.save(offering);

        MockHttpSession session = loginAsTestAdmin();

        String requestJson = """
                {
                    "recipeId": 999999,
                    "offeringType": "DINNER",
                    "includesHouseSalad": true,
                    "includesHomemadeBread": true,
                    "displayOrder": 0,
                    "prices": [
                        {
                            "amount": 20.00,
                            "displayOrder": 0
                        }
                    ]
                }
                """;

        mockMvc.perform(
                        post(
                                "/api/admin/weekly-offerings/{offeringId}/items",
                                offering.getId()
                        )
                                .session(session)
                                .with(csrf())
                                .contentType("application/json")
                                .content(requestJson)
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.message")
                                .value("Recipe not found.")
                );
    }

    @Test
    void addItemToPublishedOfferingReturnsCreated()
            throws Exception {

        Recipe recipe =
                recipeRepository.save(
                        new Recipe("Test Published Recipe")
                );

        WeeklyOffering offering = new WeeklyOffering();
        offering.setStartDate(LocalDate.of(2026, 8, 19));
        offering.setEndDate(LocalDate.of(2026, 8, 25));
        offering.setStatus(WeeklyOfferingStatus.PUBLISHED);

        offering = weeklyOfferingRepository.save(offering);

        MockHttpSession session = loginAsTestAdmin();

        String requestJson = """
                {
                    "recipeId": %d,
                    "offeringType": "DINNER",
                    "includesHouseSalad": true,
                    "includesHomemadeBread": true,
                    "displayOrder": 0,
                    "prices": [
                        {
                            "amount": 20.00,
                            "displayOrder": 0
                        }
                    ]
                }
                """.formatted(recipe.getId());

        mockMvc.perform(
                        post(
                                "/api/admin/weekly-offerings/{offeringId}/items",
                                offering.getId()
                        )
                                .session(session)
                                .with(csrf())
                                .contentType("application/json")
                                .content(requestJson)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PUBLISHED"))
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(
                        jsonPath("$.items[0].recipeName")
                                .value("Test Published Recipe")
                );
    }

    private MockHttpSession loginAsTestAdmin()
            throws Exception {

        String loginJson = """
                {
                    "username": "%s",
                    "password": "%s"
                }
                """.formatted(
                TEST_USERNAME,
                TEST_PASSWORD
        );

        return (MockHttpSession) mockMvc.perform(
                        post("/api/auth/login")
                                .contentType("application/json")
                                .content(loginJson)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getRequest()
                .getSession(false);
    }

    @Test
    void addItemWithEmptyPricesReturnsBadRequest()
            throws Exception {

        Recipe recipe =
                recipeRepository.save(
                        new Recipe("Test No Price Recipe")
                );

        WeeklyOffering offering = new WeeklyOffering();
        offering.setStartDate(LocalDate.of(2026, 8, 19));
        offering.setEndDate(LocalDate.of(2026, 8, 25));
        offering.setStatus(WeeklyOfferingStatus.DRAFT);

        offering = weeklyOfferingRepository.save(offering);

        MockHttpSession session = loginAsTestAdmin();

        String requestJson = """
                {
                    "recipeId": %d,
                    "offeringType": "DINNER",
                    "includesHouseSalad": true,
                    "includesHomemadeBread": true,
                    "displayOrder": 0,
                    "prices": []
                }
                """.formatted(recipe.getId());

        mockMvc.perform(
                        post(
                                "/api/admin/weekly-offerings/{offeringId}/items",
                                offering.getId()
                        )
                                .session(session)
                                .with(csrf())
                                .contentType("application/json")
                                .content(requestJson)
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.status")
                                .value(400)
                )
                .andExpect(
                        jsonPath("$.error")
                                .value("Bad Request")
                )
                .andExpect(
                        jsonPath("$.message")
                                .value("prices must not be empty")
                );
    }

    @Test
    void authenticatedAdminCanUpdateDraftOfferingItem()
            throws Exception {

        Recipe recipe =
                recipeRepository.save(
                        new Recipe("Test Pork Chop")
                );

        WeeklyOffering offering = new WeeklyOffering();
        offering.setStartDate(LocalDate.of(2026, 8, 19));
        offering.setEndDate(LocalDate.of(2026, 8, 25));
        offering.setStatus(WeeklyOfferingStatus.DRAFT);

        WeeklyOfferingItem item = new WeeklyOfferingItem();
        item.setRecipe(recipe);
        item.setOfferingType(OfferingType.DINNER);
        item.setIncludesHouseSalad(true);
        item.setIncludesHomemadeBread(true);
        item.setDisplayOrder(0);

        item.addPrice(
                new WeeklyOfferingItemPrice(
                        null,
                        new BigDecimal("21.95"),
                        0
                )
        );

        offering.addItem(item);

        offering = weeklyOfferingRepository.save(offering);

        Long itemId = offering.getItems().getFirst().getId();

        MockHttpSession session = loginAsTestAdmin();

        String requestJson = """
                {
                    "recipeId": %d,
                    "offeringType": "DINNER",
                    "publicDescription": "Available as a single or double.",
                    "imageUrl": null,
                    "imageAlt": null,
                    "includesHouseSalad": true,
                    "includesHomemadeBread": true,
                    "displayOrder": 0,
                    "prices": [
                        {
                            "label": "Single",
                            "amount": 21.95,
                            "displayOrder": 0
                        },
                        {
                            "label": "Double",
                            "amount": 25.95,
                            "displayOrder": 1
                        }
                    ]
                }
                """.formatted(recipe.getId());

        mockMvc.perform(
                        put(
                                "/api/admin/weekly-offerings/{offeringId}/items/{itemId}",
                                offering.getId(),
                                itemId
                        )
                                .session(session)
                                .with(csrf())
                                .contentType("application/json")
                                .content(requestJson)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.status")
                                .value("DRAFT")
                )
                .andExpect(
                        jsonPath("$.items.length()")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.items[0].prices.length()")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$.items[0].prices[0].label")
                                .value("Single")
                )
                .andExpect(
                        jsonPath("$.items[0].prices[0].amount")
                                .value(21.95)
                )
                .andExpect(
                        jsonPath("$.items[0].prices[1].label")
                                .value("Double")
                )
                .andExpect(
                        jsonPath("$.items[0].prices[1].amount")
                                .value(25.95)
                );
    }

    @Test
    void archivedOfferingItemCannotBeUpdated()
            throws Exception {

        Recipe recipe =
                recipeRepository.save(
                        new Recipe("Test Archived Pork Chop")
                );

        WeeklyOffering offering = new WeeklyOffering();
        offering.setStartDate(LocalDate.of(2026, 8, 19));
        offering.setEndDate(LocalDate.of(2026, 8, 25));
        offering.setStatus(WeeklyOfferingStatus.ARCHIVED);

        WeeklyOfferingItem item = new WeeklyOfferingItem();
        item.setRecipe(recipe);
        item.setOfferingType(OfferingType.DINNER);
        item.setIncludesHouseSalad(true);
        item.setIncludesHomemadeBread(true);
        item.setDisplayOrder(0);

        item.addPrice(
                new WeeklyOfferingItemPrice(
                        null,
                        new BigDecimal("21.95"),
                        0
                )
        );

        offering.addItem(item);

        offering = weeklyOfferingRepository.save(offering);

        Long itemId = offering.getItems().getFirst().getId();

        MockHttpSession session = loginAsTestAdmin();

        String requestJson = """
                {
                    "recipeId": %d,
                    "offeringType": "DINNER",
                    "publicDescription": "Attempted archived update.",
                    "imageUrl": null,
                    "imageAlt": null,
                    "includesHouseSalad": true,
                    "includesHomemadeBread": true,
                    "displayOrder": 0,
                    "prices": [
                        {
                            "amount": 24.95,
                            "displayOrder": 0
                        }
                    ]
                }
                """.formatted(recipe.getId());

        mockMvc.perform(
                        put(
                                "/api/admin/weekly-offerings/{offeringId}/items/{itemId}",
                                offering.getId(),
                                itemId
                        )
                                .session(session)
                                .with(csrf())
                                .contentType("application/json")
                                .content(requestJson)
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Archived weekly offerings cannot be edited."
                                )
                );
    }

    @Test
    void authenticatedAdminCanDeleteItemFromDraftOffering()
            throws Exception {

        Recipe recipe =
                recipeRepository.save(
                        new Recipe("Test Delete Recipe")
                );

        WeeklyOffering offering = new WeeklyOffering();
        offering.setStartDate(LocalDate.of(2026, 8, 19));
        offering.setEndDate(LocalDate.of(2026, 8, 25));
        offering.setStatus(WeeklyOfferingStatus.DRAFT);

        WeeklyOfferingItem item = new WeeklyOfferingItem();
        item.setRecipe(recipe);
        item.setOfferingType(OfferingType.DINNER);
        item.setDisplayOrder(0);

        item.addPrice(
                new WeeklyOfferingItemPrice(
                        null,
                        new BigDecimal("19.95"),
                        0
                )
        );

        offering.addItem(item);

        offering = weeklyOfferingRepository.save(offering);

        Long itemId = offering.getItems().getFirst().getId();

        MockHttpSession session = loginAsTestAdmin();

        mockMvc.perform(
                        delete(
                                "/api/admin/weekly-offerings/{offeringId}/items/{itemId}",
                                offering.getId(),
                                itemId
                        )
                                .session(session)
                                .with(csrf())
                )
                .andExpect(status().isNoContent());

        assertFalse(
                weeklyOfferingItemRepository.existsById(itemId)
        );
    }

    @Test
    void deleteItemFromPublishedOfferingReturnsNoContent()
            throws Exception {

        Recipe recipe =
                recipeRepository.save(
                        new Recipe("Test Published Delete Recipe")
                );

        WeeklyOffering offering = new WeeklyOffering();
        offering.setStartDate(LocalDate.of(2026, 8, 19));
        offering.setEndDate(LocalDate.of(2026, 8, 25));
        offering.setStatus(WeeklyOfferingStatus.PUBLISHED);

        WeeklyOfferingItem item = new WeeklyOfferingItem();
        item.setRecipe(recipe);
        item.setOfferingType(OfferingType.DINNER);
        item.setDisplayOrder(0);

        item.addPrice(
                new WeeklyOfferingItemPrice(
                        null,
                        new BigDecimal("19.95"),
                        0
                )
        );

        offering.addItem(item);

        offering = weeklyOfferingRepository.save(offering);

        Long itemId = offering.getItems().getFirst().getId();

        MockHttpSession session = loginAsTestAdmin();

        mockMvc.perform(
                        delete(
                                "/api/admin/weekly-offerings/{offeringId}/items/{itemId}",
                                offering.getId(),
                                itemId
                        )
                                .session(session)
                                .with(csrf())
                )
                .andExpect(status().isNoContent());

        assertFalse(
                weeklyOfferingItemRepository.existsById(itemId)
        );
    }

    @Test
    void deleteItemThatBelongsToDifferentOfferingReturnsBadRequest()
            throws Exception {

        Recipe recipe =
                recipeRepository.save(
                        new Recipe("Test Wrong Offering Recipe")
                );

        WeeklyOffering firstOffering = new WeeklyOffering();
        firstOffering.setStartDate(LocalDate.of(2026, 8, 19));
        firstOffering.setEndDate(LocalDate.of(2026, 8, 25));
        firstOffering.setStatus(WeeklyOfferingStatus.DRAFT);

        firstOffering =
                weeklyOfferingRepository.save(firstOffering);


        WeeklyOffering secondOffering = new WeeklyOffering();
        secondOffering.setStartDate(LocalDate.of(2026, 8, 26));
        secondOffering.setEndDate(LocalDate.of(2026, 9, 1));
        secondOffering.setStatus(WeeklyOfferingStatus.DRAFT);

        WeeklyOfferingItem item = new WeeklyOfferingItem();
        item.setRecipe(recipe);
        item.setOfferingType(OfferingType.DINNER);
        item.setDisplayOrder(0);

        item.addPrice(
                new WeeklyOfferingItemPrice(
                        null,
                        new BigDecimal("19.95"),
                        0
                )
        );

        secondOffering.addItem(item);

        secondOffering =
                weeklyOfferingRepository.save(secondOffering);

        Long itemId =
                secondOffering.getItems().getFirst().getId();

        MockHttpSession session = loginAsTestAdmin();

        mockMvc.perform(
                        delete(
                                "/api/admin/weekly-offerings/{offeringId}/items/{itemId}",
                                firstOffering.getId(),
                                itemId
                        )
                                .session(session)
                                .with(csrf())
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Weekly offering item not found."
                                )
                );

        assertTrue(
                weeklyOfferingItemRepository.existsById(itemId)
        );
    }

    @Test
    void archiveOffering_whenScheduled_returns200AndArchivesOffering()
            throws Exception {

        WeeklyOffering offering = new WeeklyOffering();
        offering.setStartDate(LocalDate.of(2026, 8, 19));
        offering.setEndDate(LocalDate.of(2026, 8, 25));
        offering.setStatus(WeeklyOfferingStatus.SCHEDULED);

        offering = weeklyOfferingRepository.save(offering);

        MockHttpSession session = loginAsTestAdmin();

        mockMvc.perform(
                        put("/api/admin/weekly-offerings/{offeringId}/archive",
                                offering.getId())
                                .session(session)
                                .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ARCHIVED"));

        WeeklyOffering archived =
                weeklyOfferingRepository
                        .findById(offering.getId())
                        .orElseThrow();

        assertEquals(
                WeeklyOfferingStatus.ARCHIVED,
                archived.getStatus()
        );
    }

    @Test
    void archiveOffering_whenPublished_returns200AndArchivesOffering()
            throws Exception {

        WeeklyOffering offering = new WeeklyOffering();
        offering.setStartDate(LocalDate.of(2026, 8, 19));
        offering.setEndDate(LocalDate.of(2026, 8, 25));
        offering.setStatus(WeeklyOfferingStatus.PUBLISHED);

        offering = weeklyOfferingRepository.save(offering);

        MockHttpSession session = loginAsTestAdmin();

        mockMvc.perform(
                        put("/api/admin/weekly-offerings/{offeringId}/archive",
                                offering.getId())
                                .session(session)
                                .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ARCHIVED"));
    }

    @Test
    void archiveOffering_whenDraft_returns400()
            throws Exception {

        WeeklyOffering offering = new WeeklyOffering();
        offering.setStartDate(LocalDate.of(2026, 8, 19));
        offering.setEndDate(LocalDate.of(2026, 8, 25));
        offering.setStatus(WeeklyOfferingStatus.DRAFT);

        offering = weeklyOfferingRepository.save(offering);

        MockHttpSession session = loginAsTestAdmin();

        mockMvc.perform(
                        put("/api/admin/weekly-offerings/{offeringId}/archive",
                                offering.getId())
                                .session(session)
                                .with(csrf())
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value(
                                "Draft offerings should be deleted rather than archived."
                        ));
    }

    @Test
    void addItemToArchivedOfferingReturnsBadRequest()
            throws Exception {

        Recipe recipe =
                recipeRepository.save(
                        new Recipe("Test Archived Recipe")
                );

        WeeklyOffering offering = new WeeklyOffering();
        offering.setStartDate(LocalDate.of(2026, 8, 19));
        offering.setEndDate(LocalDate.of(2026, 8, 25));
        offering.setStatus(WeeklyOfferingStatus.ARCHIVED);

        offering = weeklyOfferingRepository.save(offering);

        MockHttpSession session = loginAsTestAdmin();

        String requestJson = """
                {
                    "recipeId": %d,
                    "offeringType": "DINNER",
                    "includesHouseSalad": true,
                    "includesHomemadeBread": true,
                    "displayOrder": 0,
                    "prices": [
                        {
                            "amount": 20.00,
                            "displayOrder": 0
                        }
                    ]
                }
                """.formatted(recipe.getId());

        mockMvc.perform(
                        post(
                                "/api/admin/weekly-offerings/{offeringId}/items",
                                offering.getId()
                        )
                                .session(session)
                                .with(csrf())
                                .contentType("application/json")
                                .content(requestJson)
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Archived weekly offerings cannot be edited."
                                )
                );
    }

    @Test
    void deleteItemFromArchivedOfferingReturnsBadRequest()
            throws Exception {

        Recipe recipe =
                recipeRepository.save(
                        new Recipe("Test Archived Delete Recipe")
                );

        WeeklyOffering offering = new WeeklyOffering();
        offering.setStartDate(LocalDate.of(2026, 8, 19));
        offering.setEndDate(LocalDate.of(2026, 8, 25));
        offering.setStatus(WeeklyOfferingStatus.ARCHIVED);

        WeeklyOfferingItem item = new WeeklyOfferingItem();
        item.setRecipe(recipe);
        item.setOfferingType(OfferingType.DINNER);
        item.setDisplayOrder(0);

        item.addPrice(
                new WeeklyOfferingItemPrice(
                        null,
                        new BigDecimal("19.95"),
                        0
                )
        );

        offering.addItem(item);

        offering = weeklyOfferingRepository.save(offering);

        Long itemId = offering.getItems().getFirst().getId();

        MockHttpSession session = loginAsTestAdmin();

        mockMvc.perform(
                        delete(
                                "/api/admin/weekly-offerings/{offeringId}/items/{itemId}",
                                offering.getId(),
                                itemId
                        )
                                .session(session)
                                .with(csrf())
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Archived weekly offerings cannot be edited."
                                )
                );

        assertTrue(
                weeklyOfferingItemRepository.existsById(itemId)
        );
    }

    @Test
    void deleteOffering_whenDraft_returns204AndDeletesOffering()
            throws Exception {

        WeeklyOffering offering = new WeeklyOffering();
        offering.setStartDate(LocalDate.of(2026, 8, 19));
        offering.setEndDate(LocalDate.of(2026, 8, 25));
        offering.setStatus(WeeklyOfferingStatus.DRAFT);

        offering = weeklyOfferingRepository.save(offering);

        Long offeringId = offering.getId();

        MockHttpSession session = loginAsTestAdmin();

        mockMvc.perform(
                        delete(
                                "/api/admin/weekly-offerings/{offeringId}",
                                offeringId
                        )
                                .session(session)
                                .with(csrf())
                )
                .andExpect(status().isNoContent());

        assertFalse(
                weeklyOfferingRepository.existsById(offeringId)
        );
    }

    @Test
    void deleteOffering_whenScheduled_returns400()
            throws Exception {

        WeeklyOffering offering = new WeeklyOffering();
        offering.setStartDate(LocalDate.of(2026, 8, 19));
        offering.setEndDate(LocalDate.of(2026, 8, 25));
        offering.setStatus(WeeklyOfferingStatus.SCHEDULED);

        offering = weeklyOfferingRepository.save(offering);

        MockHttpSession session = loginAsTestAdmin();

        mockMvc.perform(
                        delete(
                                "/api/admin/weekly-offerings/{offeringId}",
                                offering.getId()
                        )
                                .session(session)
                                .with(csrf())
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Only draft offerings can be deleted."
                                )
                );

        assertTrue(
                weeklyOfferingRepository.existsById(
                        offering.getId()
                )
        );
    }

    @Test
    void deleteOffering_whenPublished_returns400()
            throws Exception {

        WeeklyOffering offering = new WeeklyOffering();
        offering.setStartDate(LocalDate.of(2026, 8, 19));
        offering.setEndDate(LocalDate.of(2026, 8, 25));
        offering.setStatus(WeeklyOfferingStatus.PUBLISHED);

        offering = weeklyOfferingRepository.save(offering);

        MockHttpSession session = loginAsTestAdmin();

        mockMvc.perform(
                        delete(
                                "/api/admin/weekly-offerings/{offeringId}",
                                offering.getId()
                        )
                                .session(session)
                                .with(csrf())
                )
                .andExpect(status().isBadRequest());

        assertTrue(
                weeklyOfferingRepository.existsById(
                        offering.getId()
                )
        );
    }

    @Test
    void deleteOffering_whenArchived_returns400()
            throws Exception {

        WeeklyOffering offering = new WeeklyOffering();
        offering.setStartDate(LocalDate.of(2026, 8, 19));
        offering.setEndDate(LocalDate.of(2026, 8, 25));
        offering.setStatus(WeeklyOfferingStatus.ARCHIVED);

        offering = weeklyOfferingRepository.save(offering);

        MockHttpSession session = loginAsTestAdmin();

        mockMvc.perform(
                        delete(
                                "/api/admin/weekly-offerings/{offeringId}",
                                offering.getId()
                        )
                                .session(session)
                                .with(csrf())
                )
                .andExpect(status().isBadRequest());

        assertTrue(
                weeklyOfferingRepository.existsById(
                        offering.getId()
                )
        );
    }

    @Test
    void authenticatedAdminCanScheduleCompleteDraftOffering()
            throws Exception {

        Recipe recipe =
                recipeRepository.save(
                        new Recipe("Test Schedule Recipe")
                );

        WeeklyOffering offering = new WeeklyOffering();
        offering.setStartDate(LocalDate.of(2026, 8, 19));
        offering.setEndDate(LocalDate.of(2026, 8, 25));
        offering.setStatus(WeeklyOfferingStatus.DRAFT);

        WeeklyOfferingItem item = new WeeklyOfferingItem();
        item.setRecipe(recipe);
        item.setOfferingType(OfferingType.DINNER);
        item.setIncludesHouseSalad(true);
        item.setIncludesHomemadeBread(true);
        item.setDisplayOrder(0);

        item.addPrice(
                new WeeklyOfferingItemPrice(
                        null,
                        new BigDecimal("21.95"),
                        0
                )
        );

        offering.addItem(item);

        offering = weeklyOfferingRepository.save(offering);

        Long offeringId = offering.getId();

        MockHttpSession session = loginAsTestAdmin();

        mockMvc.perform(
                        put(
                                "/api/admin/weekly-offerings/{offeringId}/schedule",
                                offeringId
                        )
                                .session(session)
                                .with(csrf())
                )

                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(offeringId)
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("SCHEDULED")
                )
                .andExpect(
                        jsonPath("$.items.length()")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.items[0].recipeName")
                                .value("Test Schedule Recipe")
                )
                .andExpect(
                        jsonPath("$.items[0].prices.length()")
                                .value(1)
                );
        WeeklyOffering updatedOffering =
                weeklyOfferingRepository
                        .findById(offeringId)
                        .orElseThrow();

        assertEquals(
                WeeklyOfferingStatus.SCHEDULED,
                updatedOffering.getStatus()
        );
    }

    @Test
    void emptyDraftOfferingCannotBeScheduled()
            throws Exception {

        WeeklyOffering offering = new WeeklyOffering();
        offering.setStartDate(LocalDate.of(2026, 8, 19));
        offering.setEndDate(LocalDate.of(2026, 8, 25));
        offering.setStatus(WeeklyOfferingStatus.DRAFT);

        offering = weeklyOfferingRepository.save(offering);

        MockHttpSession session = loginAsTestAdmin();

        mockMvc.perform(
                        put(
                                "/api/admin/weekly-offerings/{offeringId}/schedule",
                                offering.getId()
                        )
                                .session(session)
                                .with(csrf())
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Weekly offering must contain at least one item."
                                )
                );

        assertEquals(
                WeeklyOfferingStatus.DRAFT,
                offering.getStatus()
        );
    }

    @Test
    void offeringWithItemWithoutPriceCannotBeScheduled()
            throws Exception {

        Recipe recipe =
                recipeRepository.save(
                        new Recipe("Test Schedule No Price Recipe")
                );

        WeeklyOffering offering = new WeeklyOffering();
        offering.setStartDate(LocalDate.of(2026, 8, 19));
        offering.setEndDate(LocalDate.of(2026, 8, 25));
        offering.setStatus(WeeklyOfferingStatus.DRAFT);

        WeeklyOfferingItem item = new WeeklyOfferingItem();
        item.setRecipe(recipe);
        item.setOfferingType(OfferingType.DINNER);
        item.setDisplayOrder(0);

        offering.addItem(item);

        offering = weeklyOfferingRepository.save(offering);

        MockHttpSession session = loginAsTestAdmin();

        mockMvc.perform(
                        put(
                                "/api/admin/weekly-offerings/{offeringId}/schedule",
                                offering.getId()
                        )
                                .session(session)
                                .with(csrf())
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Every weekly offering item must have at least one price."
                                )
                );

        assertEquals(
                WeeklyOfferingStatus.DRAFT,
                offering.getStatus()
        );
    }

    @Test
    void alreadyScheduledOfferingCannotBeScheduledAgain()
            throws Exception {

        WeeklyOffering offering = new WeeklyOffering();
        offering.setStartDate(LocalDate.of(2026, 8, 19));
        offering.setEndDate(LocalDate.of(2026, 8, 25));
        offering.setStatus(WeeklyOfferingStatus.SCHEDULED);

        offering = weeklyOfferingRepository.save(offering);

        MockHttpSession session = loginAsTestAdmin();

        mockMvc.perform(
                        put(
                                "/api/admin/weekly-offerings/{offeringId}/schedule",
                                offering.getId()
                        )
                                .session(session)
                                .with(csrf())
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Only draft offerings can be scheduled."
                                )
                );

        assertEquals(
                WeeklyOfferingStatus.SCHEDULED,
                offering.getStatus()
        );
    }

    @Test
    void authenticatedAdminCanGetAllOfferings()
            throws Exception {

        WeeklyOffering olderOffering = new WeeklyOffering();
        olderOffering.setStartDate(LocalDate.of(2026, 8, 19));
        olderOffering.setEndDate(LocalDate.of(2026, 8, 25));
        olderOffering.setStatus(WeeklyOfferingStatus.DRAFT);

        weeklyOfferingRepository.save(olderOffering);

        WeeklyOffering newerOffering = new WeeklyOffering();
        newerOffering.setStartDate(LocalDate.of(2026, 8, 26));
        newerOffering.setEndDate(LocalDate.of(2026, 9, 1));
        newerOffering.setStatus(WeeklyOfferingStatus.SCHEDULED);

        weeklyOfferingRepository.save(newerOffering);

        MockHttpSession session = loginAsTestAdmin();

        mockMvc.perform(
                        get("/api/admin/weekly-offerings")
                                .session(session)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.length()")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$[0].startDate")
                                .value("2026-08-26")
                )
                .andExpect(
                        jsonPath("$[0].status")
                                .value("SCHEDULED")
                )
                .andExpect(
                        jsonPath("$[1].startDate")
                                .value("2026-08-19")
                )
                .andExpect(
                        jsonPath("$[1].status")
                                .value("DRAFT")
                );
    }

    @Test
    void authenticatedAdminCanFilterOfferingsByStatus()
            throws Exception {

        WeeklyOffering draftOffering = new WeeklyOffering();
        draftOffering.setStartDate(LocalDate.of(2026, 8, 19));
        draftOffering.setEndDate(LocalDate.of(2026, 8, 25));
        draftOffering.setStatus(WeeklyOfferingStatus.DRAFT);

        weeklyOfferingRepository.save(draftOffering);

        WeeklyOffering scheduledOffering = new WeeklyOffering();
        scheduledOffering.setStartDate(LocalDate.of(2026, 8, 26));
        scheduledOffering.setEndDate(LocalDate.of(2026, 9, 1));
        scheduledOffering.setStatus(WeeklyOfferingStatus.SCHEDULED);

        weeklyOfferingRepository.save(scheduledOffering);

        MockHttpSession session = loginAsTestAdmin();

        mockMvc.perform(
                        get("/api/admin/weekly-offerings")
                                .param("status", "DRAFT")
                                .session(session)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.length()")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$[0].status")
                                .value("DRAFT")
                )
                .andExpect(
                        jsonPath("$[0].startDate")
                                .value("2026-08-19")
                );
    }

    @Test
    void unauthenticatedUserCannotGetAdminOfferings()
            throws Exception {

        mockMvc.perform(
                        get("/api/admin/weekly-offerings")
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void authenticatedAdminCanGetOfferingById()
            throws Exception {

        Recipe recipe =
                recipeRepository.save(
                        new Recipe("Test Get Offering Recipe")
                );

        WeeklyOffering offering = new WeeklyOffering();
        offering.setStartDate(LocalDate.of(2026, 8, 19));
        offering.setEndDate(LocalDate.of(2026, 8, 25));
        offering.setStatus(WeeklyOfferingStatus.DRAFT);

        WeeklyOfferingItem item = new WeeklyOfferingItem();
        item.setRecipe(recipe);
        item.setOfferingType(OfferingType.DINNER);
        item.setPublicDescription("Test description");
        item.setIncludesHouseSalad(true);
        item.setIncludesHomemadeBread(true);
        item.setDisplayOrder(0);

        item.addPrice(
                new WeeklyOfferingItemPrice(
                        null,
                        new BigDecimal("22.95"),
                        0
                )
        );

        offering.addItem(item);

        offering = weeklyOfferingRepository.save(offering);

        Long offeringId = offering.getId();

        MockHttpSession session = loginAsTestAdmin();

        mockMvc.perform(
                        get(
                                "/api/admin/weekly-offerings/{offeringId}",
                                offeringId
                        )
                                .session(session)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(offeringId)
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("DRAFT")
                )
                .andExpect(
                        jsonPath("$.startDate")
                                .value("2026-08-19")
                )
                .andExpect(
                        jsonPath("$.endDate")
                                .value("2026-08-25")
                )
                .andExpect(
                        jsonPath("$.items.length()")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.items[0].recipeName")
                                .value("Test Get Offering Recipe")
                )
                .andExpect(
                        jsonPath("$.items[0].prices.length()")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.items[0].prices[0].amount")
                                .value(22.95)
                );
    }

    @Test
    void getNonexistentOfferingByIdReturnsBadRequest()
            throws Exception {

        MockHttpSession session = loginAsTestAdmin();

        mockMvc.perform(
                        get(
                                "/api/admin/weekly-offerings/{offeringId}",
                                999999L
                        )
                                .session(session)
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.status")
                                .value(400)
                )
                .andExpect(
                        jsonPath("$.error")
                                .value("Bad Request")
                )
                .andExpect(
                        jsonPath("$.message")
                                .value("Weekly offering not found.")
                );
    }

    @Test
    void authenticatedAdminCanUpdateDraftOfferingDates()
            throws Exception {

        WeeklyOffering offering = new WeeklyOffering();
        offering.setStartDate(LocalDate.of(2026, 8, 26));
        offering.setEndDate(LocalDate.of(2026, 8, 29));
        offering.setStatus(WeeklyOfferingStatus.DRAFT);

        offering = weeklyOfferingRepository.save(offering);

        MockHttpSession session = loginAsTestAdmin();

        String requestJson = """
                {
                    "startDate": "2026-08-27",
                    "endDate": "2026-08-30"
                }
                """;

        mockMvc.perform(
                        put(
                                "/api/admin/weekly-offerings/{offeringId}",
                                offering.getId()
                        )
                                .session(session)
                                .with(csrf())
                                .contentType("application/json")
                                .content(requestJson)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.startDate")
                                .value("2026-08-27")
                )
                .andExpect(
                        jsonPath("$.endDate")
                                .value("2026-08-30")
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("DRAFT")
                );

        WeeklyOffering updated =
                weeklyOfferingRepository
                        .findById(offering.getId())
                        .orElseThrow();

        assertEquals(
                LocalDate.of(2026, 8, 27),
                updated.getStartDate()
        );

        assertEquals(
                LocalDate.of(2026, 8, 30),
                updated.getEndDate()
        );
    }

    @Test
    void publishedOfferingCanHaveDatesChanged()
            throws Exception {

        WeeklyOffering offering = new WeeklyOffering();
        offering.setStartDate(LocalDate.of(2026, 8, 5));
        offering.setEndDate(LocalDate.of(2026, 8, 12));
        offering.setStatus(WeeklyOfferingStatus.PUBLISHED);

        offering = weeklyOfferingRepository.save(offering);

        MockHttpSession session = loginAsTestAdmin();

        String requestJson = """
                {
                    "startDate": "2026-08-06",
                    "endDate": "2026-08-13"
                }
                """;

        mockMvc.perform(
                        put(
                                "/api/admin/weekly-offerings/{offeringId}",
                                offering.getId()
                        )
                                .session(session)
                                .with(csrf())
                                .contentType("application/json")
                                .content(requestJson)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.startDate").value("2026-08-06"))
                .andExpect(jsonPath("$.endDate").value("2026-08-13"));

        WeeklyOffering updated =
                weeklyOfferingRepository
                        .findById(offering.getId())
                        .orElseThrow();

        assertEquals(
                LocalDate.of(2026, 8, 6),
                updated.getStartDate()
        );

        assertEquals(
                LocalDate.of(2026, 8, 13),
                updated.getEndDate()
        );
    }

    @Test
    void archivedOfferingCannotHaveDatesChanged()
            throws Exception {

        WeeklyOffering offering = new WeeklyOffering();
        offering.setStartDate(LocalDate.of(2026, 8, 5));
        offering.setEndDate(LocalDate.of(2026, 8, 12));
        offering.setStatus(WeeklyOfferingStatus.ARCHIVED);

        offering = weeklyOfferingRepository.save(offering);

        MockHttpSession session = loginAsTestAdmin();

        String requestJson = """
                {
                    "startDate": "2026-08-06",
                    "endDate": "2026-08-13"
                }
                """;

        mockMvc.perform(
                        put(
                                "/api/admin/weekly-offerings/{offeringId}",
                                offering.getId()
                        )
                                .session(session)
                                .with(csrf())
                                .contentType("application/json")
                                .content(requestJson)
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Archived weekly offerings cannot be edited."
                                )
                );

        WeeklyOffering unchanged =
                weeklyOfferingRepository
                        .findById(offering.getId())
                        .orElseThrow();

        assertEquals(
                LocalDate.of(2026, 8, 5),
                unchanged.getStartDate()
        );

        assertEquals(
                LocalDate.of(2026, 8, 12),
                unchanged.getEndDate()
        );
    }

}