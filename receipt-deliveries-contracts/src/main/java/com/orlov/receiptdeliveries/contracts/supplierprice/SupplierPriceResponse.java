package com.orlov.receiptdeliveries.contracts.supplierprice;

import com.orlov.receiptdeliveries.contracts.product.ProductResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Ответ сервиса с информацией о цене товара.
 *
 * @param id         идентификатор записи цены
 * @param product    товар
 * @param pricePerKg цена за килограмм
 * @param startDate  начало периода действия
 * @param endDate    конец периода действия
 */
public record SupplierPriceResponse(UUID id,
                                    ProductResponse product,
                                    BigDecimal pricePerKg,
                                    LocalDate startDate,
                                    LocalDate endDate) {
}
