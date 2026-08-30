package com.orlov.receiptdeliveries.services.product;

import com.orlov.receiptdeliveries.entities.Product;

import java.util.List;
import java.util.UUID;

/**
 * Интерфейс сервиса для работы с товарами.
 */
public interface IProductService {

    /**
     * Возвращает все товары.
     *
     * @return список всех товаров.
     */
    List<Product> getAll();

    /**
     * Получает товар по идентификатору.
     *
     * @param id идентификатор товара
     * @return найденный товар по идентификатору.
     */
    Product getById(UUID id);
}
