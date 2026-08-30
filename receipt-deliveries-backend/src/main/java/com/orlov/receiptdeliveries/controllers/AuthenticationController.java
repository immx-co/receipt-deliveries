package com.orlov.receiptdeliveries.controllers;

import com.orlov.receiptdeliveries.contracts.authorization.LoginRequest;
import com.orlov.receiptdeliveries.contracts.authorization.LoginResponse;
import com.orlov.receiptdeliveries.services.authorization.IAuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер авторизации.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final IAuthenticationService authenticationService;

    /**
     * Выполняет вход.
     *
     * @param loginRequest запрос на авторизацию
     * @return токен и сведения об организации.
     */
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        return authenticationService.login(loginRequest);
    }
}
