package com.orlov.receiptdeliveries.client;

import com.orlov.receiptdeliveries.contracts.authorization.OrganizationRole;
import com.orlov.receiptdeliveries.contracts.organization.OrganizationResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;

/**
 * Клиент для организаций.
 */
public class OrganizationClient {

    private final RestClient restClient;

    public OrganizationClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<OrganizationResponse> getAllByRole(OrganizationRole role, String accessToken) {
        List<OrganizationResponse> response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/organizations")
                        .queryParam("role", role)
                        .build())
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .body(new ParameterizedTypeReference<List<OrganizationResponse>>() {
                });

        if(response == null)
            throw new IllegalStateException("Empty response");

        return response;
    }
}
