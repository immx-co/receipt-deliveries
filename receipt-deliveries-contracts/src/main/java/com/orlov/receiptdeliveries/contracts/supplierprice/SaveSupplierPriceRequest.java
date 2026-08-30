package com.orlov.receiptdeliveries.contracts.supplierprice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Запрос на создание или изменение цены поставщика.
 *
 * @param productId  идентификатор продукта
 * @param pricePerKg цена за килограмм
 * @param startDate  начало периода действия цены
 * @param endDate    конец периода действия цены
 */
public record SaveSupplierPriceRequest(UUID productId,
                                       BigDecimal pricePerKg,
                                       LocalDate startDate,
                                       LocalDate endDate) {
}
