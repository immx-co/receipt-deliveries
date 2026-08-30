package com.orlov.receiptdeliveries.contracts.product;

import java.util.UUID;

/**
 * Информация о товаре.
 *
 * @param id        идентификатор товара
 * @param fruitType тип фрукта
 * @param variety   сорт фрукта
 */
public record ProductResponse(UUID id,
                              FruitType fruitType,
                              FruitVariety variety) {
}
