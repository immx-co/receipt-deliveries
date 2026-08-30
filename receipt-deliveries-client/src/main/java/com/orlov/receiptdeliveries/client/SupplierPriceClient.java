package com.orlov.receiptdeliveries.client;

import com.orlov.receiptdeliveries.contracts.supplierprice.SaveSupplierPriceRequest;
import com.orlov.receiptdeliveries.contracts.supplierprice.SupplierPriceResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Клиент для работы с ценами товаров.
 */
public class SupplierPriceClient {

    private final RestClient restClient;

    public SupplierPriceClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<SupplierPriceResponse> getAll(UUID supplierId,
                                              String accessToken) {
        List<SupplierPriceResponse> response = restClient.get()
                .uri(
                        "/api/v1/suppliers/{supplierId}/prices",
                        supplierId)
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .body(new ParameterizedTypeReference<List<SupplierPriceResponse>>() {
                });

        if(response == null)
            throw new IllegalStateException("Empty response");

        return response;
    }

    public List<SupplierPriceResponse> getActive(UUID supplierId,
                                                 LocalDate date,
                                                 String accessToken) {
        List<SupplierPriceResponse> response = restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/suppliers/{supplierId}/prices/active")
                        .queryParam(
                                "date",
                                date)
                        .build(supplierId))
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .body(new ParameterizedTypeReference<List<SupplierPriceResponse>>() {
                });

        if(response == null)
            throw new IllegalStateException("Empty response");

        return response;
    }

    public SupplierPriceResponse create(UUID supplierId,
                                        SaveSupplierPriceRequest request,
                                        String accessToken) {
        SupplierPriceResponse response = restClient.post()
                .uri(
                        "/api/v1/suppliers/{supplierId}/prices",
                        supplierId)
                .headers(headers -> headers.setBearerAuth(accessToken))
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(SupplierPriceResponse.class);

        if(response == null)
            throw new IllegalStateException("Empty response");

        return response;
    }

    public SupplierPriceResponse update(UUID supplierId,
                                        UUID priceId,
                                        SaveSupplierPriceRequest request,
                                        String accessToken) {
        SupplierPriceResponse response = restClient.put()
                .uri(
                        "/api/v1/suppliers/{supplierId}/prices/{priceId}",
                        supplierId,
                        priceId)
                .headers(headers -> headers.setBearerAuth(accessToken))
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(SupplierPriceResponse.class);

        if(response == null)
            throw new IllegalStateException("Empty response");

        return response;
    }

    public void delete(UUID supplierId,
                       UUID priceId,
                       String accessToken) {
        restClient.delete()
                .uri(
                        "/api/v1/suppliers/{supplierId}/prices/{priceId}",
                        supplierId,
                        priceId)
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .toBodilessEntity();
    }
}
