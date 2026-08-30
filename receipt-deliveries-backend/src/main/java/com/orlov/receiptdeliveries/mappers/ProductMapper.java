package com.orlov.receiptdeliveries.mappers;

import com.orlov.receiptdeliveries.contracts.product.ProductResponse;
import com.orlov.receiptdeliveries.entities.Product;
import org.springframework.stereotype.Component;

/**
 * Преобразует модель товара в API контракт.
 */
@Component
public class ProductMapper {

    /**
     * Преобразует товар в ответ API.
     *
     * @param product модель товара
     * @return ответ сервиса с информацией о товаре.
     */
    public ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getFruitType(),
                product.getVariety());
    }
}
