package com.orlov.receiptdeliveries.configuration;

import com.orlov.receiptdeliveries.client.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

/**
 * Автоматическая конфигурация клиентов.
 */
@AutoConfiguration
@EnableConfigurationProperties(ReceiptDeliveriesApiProperties.class)
public class ReceiptDeliveriesApiClientAutoConfiguration {

    @Bean(name = "receiptDeliveriesRestClient")
    @ConditionalOnMissingBean(name = "receiptDeliveriesRestClient")
    public RestClient receiptDeliveriesRestClient(RestClient.Builder builder,
                                                  ReceiptDeliveriesApiProperties properties) {
        return builder.baseUrl(properties.baseUrl())
                .build();
    }

    @Bean
    @ConditionalOnMissingBean(AuthenticationClient.class)
    public AuthenticationClient authenticationClient(@Qualifier("receiptDeliveriesRestClient") RestClient restClient) {
        return new AuthenticationClient(restClient);
    }

    @Bean
    @ConditionalOnMissingBean(OrganizationClient.class)
    public OrganizationClient organizationClient(@Qualifier("receiptDeliveriesRestClient") RestClient restClient) {
        return new OrganizationClient(restClient);
    }

    @Bean
    @ConditionalOnMissingBean(ProductClient.class)
    public ProductClient productClient(@Qualifier("receiptDeliveriesRestClient") RestClient restClient) {
        return new ProductClient(restClient);
    }

    @Bean
    @ConditionalOnMissingBean(SupplierPriceClient.class)
    public SupplierPriceClient supplierPriceClient(@Qualifier("receiptDeliveriesRestClient") RestClient restClient) {
        return new SupplierPriceClient(restClient);
    }

    @Bean
    @ConditionalOnMissingBean(DeliveryClient.class)
    public DeliveryClient deliveryClient(@Qualifier("receiptDeliveriesRestClient") RestClient restClient) {
        return new DeliveryClient(restClient);
    }
}
