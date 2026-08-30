package com.orlov.receiptdeliveries.contracts.delivery;

import com.orlov.receiptdeliveries.contracts.product.FruitType;
import com.orlov.receiptdeliveries.contracts.product.FruitVariety;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Строка отчета по поставкам.
 *
 * @param supplierId    идентификатор поставщика
 * @param supplierName  название поставщика
 * @param productId     идентификатор продукта
 * @param fruitType     тип фрукта
 * @param variety       сорт фрукта
 * @param totalWeightKg общий вес продукта
 * @param totalCost     общая стоимость продукта
 */
public record DeliveryReportItemResponse(UUID supplierId,
                                         String supplierName,
                                         UUID productId,
                                         FruitType fruitType,
                                         FruitVariety variety,
                                         BigDecimal totalWeightKg,
                                         BigDecimal totalCost) {
}
