package com.orlov.receiptdeliveries.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Настройка подключения к Backend сервису.
 *
 * @param baseUrl базовый адрес backend сервиса.
 */
@ConfigurationProperties(prefix = "receipt-deliveries.api")
public record ReceiptDeliveriesApiProperties(String baseUrl) {
}
