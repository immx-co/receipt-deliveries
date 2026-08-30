package com.orlov.receiptdeliveries.client;

import com.orlov.receiptdeliveries.contracts.authorization.LoginRequest;
import com.orlov.receiptdeliveries.contracts.authorization.LoginResponse;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

/**
 * Клиент для авторизации.
 */
public class AuthenticationClient {

    private final RestClient restClient;

    public AuthenticationClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public LoginResponse login(String login, String password) {
        LoginRequest request = new LoginRequest(login, password);

        LoginResponse response = restClient.post()
                .uri("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(LoginResponse.class);

        if(response == null)
            throw new IllegalStateException("Empty response");

        return response;
    }
}
