package com.orlov.receiptdeliveries.contracts.delivery;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Запрос на позицию регистрируемой поставки.
 *
 * @param productId идентификатор поставляемого продукта
 * @param weightKg  вес продукта в килограммах
 */
public record CreateDeliveryItemRequest(@NotNull(message = "Продукт не указан.") UUID productId,
                                        @NotNull(message = "Вес продукта не указан.") @DecimalMin(
                                                value = "0.001",
                                                message = "Вес продукта должен быть больше нуля."
                                        ) @Digits(
                                                integer = 9,
                                                fraction = 3,
                                                message = "Вес должен содержать не более трёх знаков после запятой."
                                        ) BigDecimal weightKg) {
}
