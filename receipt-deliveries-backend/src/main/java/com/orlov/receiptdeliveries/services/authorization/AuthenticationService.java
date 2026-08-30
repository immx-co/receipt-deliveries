package com.orlov.receiptdeliveries.services.authorization;

import com.orlov.receiptdeliveries.contracts.authorization.LoginRequest;
import com.orlov.receiptdeliveries.contracts.authorization.LoginResponse;
import com.orlov.receiptdeliveries.entities.Organization;
import com.orlov.receiptdeliveries.services.organization.IOrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

/**
 * Сервис аутентификации.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthenticationService implements IAuthenticationService {

    private final IOrganizationService organizationService;

    private final PasswordEncoder passwordEncoder;

    private final IJwtService jwtService;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        if(loginRequest == null || loginRequest.login() == null || loginRequest.login()
                .isBlank() || loginRequest.password() == null || loginRequest.password()
                   .isBlank()) {
            throw new BadCredentialsException("Неверный логин или пароль.");
        }

        String normalizedLogin = loginRequest.login()
                .trim();

        Organization organization;

        try {
            organization = organizationService.getByLogin(normalizedLogin);
        } catch(NoSuchElementException ex) {
            throw new BadCredentialsException("Неверный логин или пароль.");
        }

        if(!passwordEncoder.matches(
                loginRequest.password(),
                organization.getPasswordHash())) {
            throw new BadCredentialsException("Неверный логин или пароль.");
        }

        IJwtService.GeneratedToken generatedToken = jwtService.generateAccessToken(organization);

        return new LoginResponse(
                generatedToken.value(),
                "Bearer",
                generatedToken.expiresAt(),
                organization.getId(),
                organization.getName(),
                organization.getLogin(),
                organization.getRole());
    }
}
