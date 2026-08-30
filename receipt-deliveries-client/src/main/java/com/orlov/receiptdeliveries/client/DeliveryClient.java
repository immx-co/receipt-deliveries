package com.orlov.receiptdeliveries.client;

import com.orlov.receiptdeliveries.contracts.delivery.CreateDeliveryRequest;
import com.orlov.receiptdeliveries.contracts.delivery.DeliveryReportResponse;
import com.orlov.receiptdeliveries.contracts.delivery.DeliveryResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Клиент для работы с поставками.
 */
public class DeliveryClient {

    private final RestClient restClient;

    public DeliveryClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public DeliveryResponse register(UUID receiverId,
                                     CreateDeliveryRequest request,
                                     String accessToken) {
        DeliveryResponse response = restClient.post()
                .uri(
                        "/api/v1/receivers/{receiverId}/deliveries",
                        receiverId)
                .headers(headers -> headers.setBearerAuth(accessToken))
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(DeliveryResponse.class);

        if(response == null)
            throw new IllegalStateException("Empty response");

        return response;
    }

    public List<DeliveryResponse> getAllForSupplier(UUID supplierId,
                                                    String accessToken) {
        List<DeliveryResponse> response = restClient.get()
                .uri(
                        "/api/v1/suppliers/{supplierId}/deliveries",
                        supplierId)
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .body(new ParameterizedTypeReference<List<DeliveryResponse>>() {
                });

        if(response == null)
            throw new IllegalStateException("Empty response");

        return response;
    }

    public List<DeliveryResponse> getAllForReceiver(UUID receiverId,
                                                    String accessToken) {
        List<DeliveryResponse> response = restClient.get()
                .uri(
                        "/api/v1/receivers/{receiverId}/deliveries",
                        receiverId)
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        if(response == null)
            throw new IllegalStateException("Empty response");

        return response;
    }

    public DeliveryResponse getById(UUID organizationId,
                                    UUID deliveryId,
                                    String accessToken) {
        DeliveryResponse response = restClient.get()
                .uri(
                        "/api/v1/organizations/{organizationId}/deliveries/{deliveryId}",
                        organizationId,
                        deliveryId)
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .body(DeliveryResponse.class);

        if(response == null)
            throw new IllegalStateException("Empty response");

        return response;
    }

    public DeliveryReportResponse getReport(UUID receiverId,
                                            OffsetDateTime from,
                                            OffsetDateTime to,
                                            String accessToken) {
        DeliveryReportResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/receivers/{receiverId}/deliveries/report")
                        .queryParam(
                                "from",
                                "{from}")
                        .queryParam(
                                "to",
                                "{to}")
                        .build(Map.of(
                                "receiverId",
                                receiverId,
                                "from",
                                from.toString(),
                                "to",
                                to.toString())))
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .body(DeliveryReportResponse.class);

        if(response == null)
            throw new IllegalStateException("Empty response");

        return response;
    }
}
