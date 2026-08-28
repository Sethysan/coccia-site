package com.cocciahouse.api.config;

import com.cocciahouse.api.model.AdminUser;
import com.cocciahouse.api.model.AdminUserRole;
import com.cocciahouse.api.repository.AdminUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminUserInitializer implements CommandLineRunner {

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${ADMIN_USERNAME:}")
    private String adminUsername;

    @Value("${ADMIN_PASSWORD:}")
    private String adminPassword;

    public AdminUserInitializer(
            AdminUserRepository adminUserRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.adminUserRepository = adminUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        if (adminUsername.isBlank() || adminPassword.isBlank()) {
            return;
        }

        if (adminUserRepository.findByUsernameIgnoreCase(adminUsername).isPresent()) {
            return;
        }

        String passwordHash =
                passwordEncoder.encode(adminPassword);

        AdminUser adminUser =
                new AdminUser(
                        adminUsername,
                        passwordHash,
                        adminUsername,
                        AdminUserRole.ADMIN
                );

        adminUserRepository.save(adminUser);
    }
}