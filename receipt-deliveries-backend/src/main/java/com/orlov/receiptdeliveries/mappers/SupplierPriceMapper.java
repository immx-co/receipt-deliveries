package com.orlov.receiptdeliveries.mappers;

import com.orlov.receiptdeliveries.contracts.supplierprice.SupplierPriceResponse;
import com.orlov.receiptdeliveries.entities.SupplierPrice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Преобразует модель записи цены товара в API контракт.
 */
@Component
@RequiredArgsConstructor
public class SupplierPriceMapper {

    /**
     * Маппер товаров.
     */
    private final ProductMapper productMapper;

    /**
     * Преобразует модель записи цены товара в ответ API.
     *
     * @param supplierPrice цена товара
     * @return ответ сервиса с информацией о цене товара.
     */
    public SupplierPriceResponse toResponse(SupplierPrice supplierPrice) {

        return new SupplierPriceResponse(
                supplierPrice.getId(),
                productMapper.toResponse(supplierPrice.getProduct()),
                supplierPrice.getPricePerKg(),
                supplierPrice.getStartPriceEffect(),
                supplierPrice.getEndPriceEffect());
    }
}
