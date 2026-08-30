package com.orlov.receiptdeliveries.contracts.delivery;

import com.orlov.receiptdeliveries.contracts.deliveryitem.DeliveryItemResponse;
import com.orlov.receiptdeliveries.contracts.organization.OrganizationResponse;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Ответ сервиса с информацией о поставке.
 *
 * @param id            идентификатор поставки
 * @param supplier      поставщик
 * @param receiver      приемщик
 * @param deliveryAt    дата и время поставки
 * @param items         позиции поставки
 * @param totalWeightKg общий вес поставки
 * @param totalCost     общая стоимость поставки
 */
public record DeliveryResponse(UUID id,
                               OrganizationResponse supplier,
                               OrganizationResponse receiver,
                               OffsetDateTime deliveryAt,
                               List<DeliveryItemResponse> items,
                               BigDecimal totalWeightKg,
                               BigDecimal totalCost) {
}
