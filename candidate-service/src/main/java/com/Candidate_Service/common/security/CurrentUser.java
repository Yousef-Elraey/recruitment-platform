package com.Candidate_Service.common.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class CurrentUser {
    public static AuthenticatedUser getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        return (AuthenticatedUser) authentication.getPrincipal();
    }
}
