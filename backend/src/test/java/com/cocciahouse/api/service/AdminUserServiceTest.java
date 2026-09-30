package com.cocciahouse.api.service;

import com.cocciahouse.api.dto.user.UpdateAdminUserRequest;
import com.cocciahouse.api.dto.user.ResetAdminUserPasswordRequest;
import com.cocciahouse.api.model.AdminUser;
import com.cocciahouse.api.model.AdminUserRole;
import com.cocciahouse.api.repository.AdminUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceTest {

    @Mock
    private AdminUserRepository adminUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AdminSessionService adminSessionService;

    private AdminUserService adminUserService;

    @BeforeEach
    void setUp() {
        adminUserService = new AdminUserService(
                adminUserRepository,
                passwordEncoder,
                adminSessionService
        );
    }

    @Test
    void deactivatingUserRevokesExistingSessions() {
        AdminUser staffUser = new AdminUser(
                "staff",
                "password-hash",
                "Staff User",
                AdminUserRole.STAFF
        );

        when(adminUserRepository.findById(1L))
                .thenReturn(Optional.of(staffUser));

        when(adminUserRepository.save(any(AdminUser.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UpdateAdminUserRequest request =
                new UpdateAdminUserRequest(
                        "Staff User",
                        "STAFF",
                        false
                );

        adminUserService.updateUser(
                1L,
                request,
                "admin"
        );

        verify(adminSessionService)
                .revokeSessions("staff");
    }

    @Test
    void changingRoleRevokesExistingSessions() {
        AdminUser staffUser = new AdminUser(
                "staff",
                "password-hash",
                "Staff User",
                AdminUserRole.STAFF
        );

        when(adminUserRepository.findById(1L))
                .thenReturn(Optional.of(staffUser));

        when(adminUserRepository.save(any(AdminUser.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UpdateAdminUserRequest request =
                new UpdateAdminUserRequest(
                        "Staff User",
                        "ADMIN",
                        true
                );

        adminUserService.updateUser(
                1L,
                request,
                "admin"
        );

        verify(adminSessionService)
                .revokeSessions("staff");
    }

    @Test
    void changingOnlyDisplayNameDoesNotRevokeSession() {
        AdminUser staffUser = new AdminUser(
                "staff",
                "password-hash",
                "Staff User",
                AdminUserRole.STAFF
        );

        when(adminUserRepository.findById(1L))
                .thenReturn(Optional.of(staffUser));

        when(adminUserRepository.save(any(AdminUser.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UpdateAdminUserRequest request =
                new UpdateAdminUserRequest(
                        "Updated Staff Name",
                        "STAFF",
                        true
                );

        adminUserService.updateUser(
                1L,
                request,
                "admin"
        );

        verify(adminSessionService, never())
                .revokeSessions("staff");
    }

    @Test
    void resettingPasswordRevokesExistingSessions() {
        AdminUser staffUser = new AdminUser(
                "staff",
                "old-password-hash",
                "Staff User",
                AdminUserRole.STAFF
        );

        when(adminUserRepository.findById(1L))
                .thenReturn(Optional.of(staffUser));

        when(passwordEncoder.encode("new-password"))
                .thenReturn("new-password-hash");

        ResetAdminUserPasswordRequest request =
                new ResetAdminUserPasswordRequest(
                        "new-password"
                );

        adminUserService.resetPassword(
                1L,
                request
        );

        verify(adminUserRepository)
                .save(staffUser);

        verify(adminSessionService)
                .revokeSessions("staff");
    }

}