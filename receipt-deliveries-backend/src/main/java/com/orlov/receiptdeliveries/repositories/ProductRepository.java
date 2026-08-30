package com.orlov.receiptdeliveries.repositories;

import com.orlov.receiptdeliveries.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Репозиторий для работы с продуктами.
 */
public interface ProductRepository extends JpaRepository<Product, UUID> {

    /**
     * Поиск всех продуктов в отсортированном порядке.
     * @return список продуктов в отсортированном порядке.
     */
    List<Product> findAllByOrderByFruitTypeAscVarietyAsc();
}
