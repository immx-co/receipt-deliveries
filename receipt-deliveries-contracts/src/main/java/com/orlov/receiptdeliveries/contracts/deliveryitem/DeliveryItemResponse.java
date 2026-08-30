package com.orlov.receiptdeliveries.contracts.deliveryitem;

import com.orlov.receiptdeliveries.contracts.product.ProductResponse;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Ответ сервиса с позицией зарегистрированной поставки.
 *
 * @param id         идентификатор позиции
 * @param product    поставленный продукт
 * @param weightKg   вес продукта
 * @param pricePerKg цена за килограмм на момент поставки
 * @param totalCost  стоимость позиции
 */
public record DeliveryItemResponse(UUID id,
                                   ProductResponse product,
                                   BigDecimal weightKg,
                                   BigDecimal pricePerKg,
                                   BigDecimal totalCost) {
}
