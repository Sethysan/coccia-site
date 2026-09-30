package com.cocciahouse.api.config;

import com.cocciahouse.api.repository.AdminUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AdminUserInitializerTest {

    @Test
    void databaseFailureDuringInitializationDoesNotPreventApplicationStartup() {

        AdminUserRepository adminUserRepository =
                mock(AdminUserRepository.class);

        PasswordEncoder passwordEncoder =
                mock(PasswordEncoder.class);

        AdminUserInitializer initializer =
                new AdminUserInitializer(
                        adminUserRepository,
                        passwordEncoder
                );

        ReflectionTestUtils.setField(
                initializer,
                "adminUsername",
                "test-admin"
        );

        ReflectionTestUtils.setField(
                initializer,
                "adminPassword",
                "test-password"
        );

        when(adminUserRepository.findByUsernameIgnoreCase("test-admin"))
                .thenThrow(
                        new DataAccessResourceFailureException(
                                "Simulated database connection failure"
                        )
                );

        assertDoesNotThrow(() -> initializer.run());
    }
}