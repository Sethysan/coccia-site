package com.cocciahouse.api.controller.admin;

import com.cocciahouse.api.dto.user.AdminUserResponse;
import com.cocciahouse.api.dto.user.CreateAdminUserRequest;
import com.cocciahouse.api.dto.user.ResetAdminUserPasswordRequest;
import com.cocciahouse.api.dto.user.UpdateAdminUserRequest;
import com.cocciahouse.api.service.AdminUserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(
            AdminUserService adminUserService
    ) {
        this.adminUserService = adminUserService;
    }

    @GetMapping
    public ResponseEntity<List<AdminUserResponse>> getAllUsers() {
        return ResponseEntity.ok(
                adminUserService.getAllUsers()
        );
    }

    @PostMapping
    public ResponseEntity<AdminUserResponse> createUser(
            @Valid @RequestBody CreateAdminUserRequest request
    ) {
        AdminUserResponse createdUser =
                adminUserService.createUser(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdminUserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAdminUserRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                adminUserService.updateUser(
                        id,
                        request,
                        authentication.getName()
                )
        );
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<Void> resetPassword(
            @PathVariable Long id,
            @Valid @RequestBody ResetAdminUserPasswordRequest request
    ) {
        adminUserService.resetPassword(id, request);

        return ResponseEntity.noContent().build();
    }
}