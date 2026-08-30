package com.orlov.receiptdeliveries.frontend.session;

import com.orlov.receiptdeliveries.contracts.authorization.LoginResponse;
import com.orlov.receiptdeliveries.contracts.authorization.OrganizationRole;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
@VaadinSessionScope
public class OrganizationSession {

    private LoginResponse loginResponse;

    public void authenticate(LoginResponse loginResponse) {
        this.loginResponse = loginResponse;
    }

    public boolean isAuthenticated() {
        if(loginResponse == null || loginResponse.accessToken() == null || loginResponse.accessToken()
                .isBlank() || loginResponse.expiresAt() == null || !Instant.now()
                .isBefore(loginResponse.expiresAt())) {
            logout();

            return false;
        }

        return true;
    }

    public String getAccessToken() {
        return isAuthenticated() ? loginResponse.accessToken() : null;
    }

    public UUID getOrganizationId() {
        return isAuthenticated() ? loginResponse.organizationId() : null;
    }

    public String getOrganizationName() {
        return isAuthenticated() ? loginResponse.organizationName() : null;
    }

    public OrganizationRole getRole() {
        return isAuthenticated() ? loginResponse.role() : null;
    }

    public void logout() {
        loginResponse = null;
    }
}
