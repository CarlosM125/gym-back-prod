package com.example.gymbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
public class AuditConfig {

    @Bean
    public AuditorAware<Long> auditorProvider() {
        return () -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
                return Optional.of(-1L); // System/anonymous operations
            }
            // Return a fixed ID for now; can be enhanced to look up user ID from username
            return Optional.of(0L);
        };
    }
}
