package com.cocciahouse.api.service;

import com.cocciahouse.api.dto.user.AdminUserResponse;
import com.cocciahouse.api.dto.user.CreateAdminUserRequest;
import com.cocciahouse.api.dto.user.ResetAdminUserPasswordRequest;
import com.cocciahouse.api.dto.user.UpdateAdminUserRequest;
import com.cocciahouse.api.model.AdminUser;
import com.cocciahouse.api.model.AdminUserRole;
import com.cocciahouse.api.repository.AdminUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminUserService {

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserService(
            AdminUserRepository adminUserRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.adminUserRepository = adminUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<AdminUserResponse> getAllUsers() {
        return adminUserRepository
                .findAllByOrderByDisplayNameAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public AdminUserResponse createUser(
            CreateAdminUserRequest request
    ) {

        String username = request.username().trim();

        if (adminUserRepository.existsByUsernameIgnoreCase(username)) {
            throw new IllegalArgumentException(
                    "A user with that username already exists."
            );
        }

        AdminUserRole role = parseRole(request.role());

        AdminUser adminUser = new AdminUser(
                username,
                passwordEncoder.encode(request.password()),
                request.displayName().trim(),
                role
        );

        AdminUser savedUser =
                adminUserRepository.save(adminUser);

        return toResponse(savedUser);
    }

    @Transactional
    public AdminUserResponse updateUser(
            Long id,
            UpdateAdminUserRequest request,
            String currentUsername
    ) {

        AdminUser adminUser =
                getUserById(id);

        AdminUserRole newRole =
                parseRole(request.role());

        boolean editingSelf =
                adminUser.getUsername()
                        .equalsIgnoreCase(currentUsername);

        if (
                editingSelf &&
                        (
                                !request.active() ||
                                        newRole != AdminUserRole.ADMIN
                        )
        ) {
            throw new IllegalArgumentException(
                    "You cannot remove your own administrator access."
            );
        }

        if (
                adminUser.isActive() &&
                        adminUser.getRole() == AdminUserRole.ADMIN &&
                        (
                                !request.active() ||
                                        newRole != AdminUserRole.ADMIN
                        )
        ) {
            ensureAnotherActiveAdminExists(adminUser.getId());
        }

        adminUser.setDisplayName(
                request.displayName().trim()
        );

        adminUser.setRole(newRole);
        adminUser.setActive(request.active());

        return toResponse(
                adminUserRepository.save(adminUser)
        );
    }

    @Transactional
    public void resetPassword(
            Long id,
            ResetAdminUserPasswordRequest request
    ) {

        AdminUser adminUser =
                getUserById(id);

        adminUser.setPasswordHash(
                passwordEncoder.encode(
                        request.password()
                )
        );

        adminUserRepository.save(adminUser);
    }

    private AdminUser getUserById(Long id) {
        return adminUserRepository
                .findById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "User not found."
                        )
                );
    }

    private AdminUserRole parseRole(String role) {
        try {
            return AdminUserRole.valueOf(
                    role.trim().toUpperCase()
            );
        } catch (
                IllegalArgumentException |
                NullPointerException exception
        ) {
            throw new IllegalArgumentException(
                    "Role must be ADMIN or STAFF."
            );
        }
    }

    private void ensureAnotherActiveAdminExists(
            Long excludedUserId
    ) {

        boolean anotherActiveAdminExists =
                adminUserRepository
                        .findAll()
                        .stream()
                        .anyMatch(user ->
                                !user.getId().equals(excludedUserId) &&
                                        user.isActive() &&
                                        user.getRole() == AdminUserRole.ADMIN
                        );

        if (!anotherActiveAdminExists) {
            throw new IllegalArgumentException(
                    "At least one active administrator must remain."
            );
        }
    }

    private AdminUserResponse toResponse(
            AdminUser adminUser
    ) {
        return new AdminUserResponse(
                adminUser.getId(),
                adminUser.getUsername(),
                adminUser.getDisplayName(),
                adminUser.getRole().name(),
                adminUser.isActive(),
                adminUser.getCreatedAt(),
                adminUser.getUpdatedAt()
        );
    }
}