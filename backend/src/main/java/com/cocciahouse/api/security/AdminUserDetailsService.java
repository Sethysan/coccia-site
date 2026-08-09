package com.cocciahouse.api.security;

import com.cocciahouse.api.model.AdminUser;
import com.cocciahouse.api.repository.AdminUserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AdminUserDetailsService implements UserDetailsService {

    private final AdminUserRepository adminUserRepository;

    public AdminUserDetailsService(
            AdminUserRepository adminUserRepository
    ) {
        this.adminUserRepository = adminUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        AdminUser adminUser = adminUserRepository
                .findByUsernameIgnoreCase(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Admin user not found."
                        )
                );

        return User.builder()
                .username(adminUser.getUsername())
                .password(adminUser.getPasswordHash())
                .roles("ADMIN")
                .disabled(!adminUser.isActive())
                .build();
    }
}