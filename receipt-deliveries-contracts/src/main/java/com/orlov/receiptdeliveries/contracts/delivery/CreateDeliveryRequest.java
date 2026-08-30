package com.orlov.receiptdeliveries.contracts.delivery;

import jakarta.validation.Valid;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Запрос на регистрацию фактически поступившей поставки.
 *
 * @param supplierId идентификатор поставщика
 * @param deliveryAt дата и время фактической поставки
 * @param items      позиция поставки
 */
public record CreateDeliveryRequest(UUID supplierId,
                                    OffsetDateTime deliveryAt,
                                    List<@Valid CreateDeliveryItemRequest> items) {
}
