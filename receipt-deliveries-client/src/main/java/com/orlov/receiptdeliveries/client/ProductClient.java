package com.orlov.receiptdeliveries.client;

import com.orlov.receiptdeliveries.contracts.product.ProductResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;

/**
 * Клиент для товаров.
 */
public class ProductClient {

    private final RestClient restClient;

    public ProductClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<ProductResponse> getAll(String accessToken) {
        List<ProductResponse> response = restClient.get()
                .uri("/api/v1/products")
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .body(new ParameterizedTypeReference<List<ProductResponse>>() {
                });

        if(response == null)
            throw new IllegalStateException("Empty response");

        return response;
    }
}
