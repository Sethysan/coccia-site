package com.cocciahouse.api.controller.auth;

import com.cocciahouse.api.dto.AuthSessionResponse;
import com.cocciahouse.api.dto.LoginRequest;
import com.cocciahouse.api.model.AdminUser;
import com.cocciahouse.api.repository.AdminUserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.web.csrf.CsrfToken;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final SecurityContextRepository securityContextRepository =
            new HttpSessionSecurityContextRepository();

    private final AdminUserRepository adminUserRepository;

    public AuthController(
            AuthenticationManager authenticationManager,
            AdminUserRepository adminUserRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.adminUserRepository = adminUserRepository;
    }


    @PostMapping("/login")
    public ResponseEntity<AuthSessionResponse> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        UsernamePasswordAuthenticationToken authenticationRequest =
                UsernamePasswordAuthenticationToken.unauthenticated(
                        loginRequest.username(),
                        loginRequest.password()
                );

        Authentication authentication =
                authenticationManager.authenticate(authenticationRequest);

        SecurityContext securityContext =
                SecurityContextHolder.createEmptyContext();

        securityContext.setAuthentication(authentication);

        SecurityContextHolder.setContext(securityContext);

        securityContextRepository.saveContext(
                securityContext,
                request,
                response
        );

        return ResponseEntity.ok(
                buildAuthenticatedSession(
                        authentication.getName()
                )
        );
    }


    @GetMapping("/session")
    public ResponseEntity<AuthSessionResponse> getSession(
            Authentication authentication
    ) {

        if (
                authentication == null ||
                        !authentication.isAuthenticated()
        ) {
            return ResponseEntity.ok(
                    new AuthSessionResponse(
                            false,
                            null,
                            null,
                            null
                    )
            );
        }

        return ResponseEntity.ok(
                buildAuthenticatedSession(
                        authentication.getName()
                )
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthSessionResponse> logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {

        new SecurityContextLogoutHandler().logout(
                request,
                response,
                authentication
        );

        return ResponseEntity.ok(
                new AuthSessionResponse(
                        false,
                        null,
                        null,
                        null
                )
        );
    }

    @GetMapping("/csrf")
    public ResponseEntity<Void> csrf(
            CsrfToken csrfToken
    ) {

        return ResponseEntity.ok().build();
    }

    private AuthSessionResponse buildAuthenticatedSession(
            String username
    ) {

        AdminUser adminUser =
                adminUserRepository
                        .findByUsernameIgnoreCase(username)
                        .orElseThrow();

        return new AuthSessionResponse(
                true,
                adminUser.getUsername(),
                adminUser.getDisplayName(),
                adminUser.getRole().name()
        );
    }

}