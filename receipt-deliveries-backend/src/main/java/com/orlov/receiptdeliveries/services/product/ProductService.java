package com.orlov.receiptdeliveries.services.product;

import com.orlov.receiptdeliveries.entities.Product;
import com.orlov.receiptdeliveries.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Сервис для работы с товарами.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService implements IProductService {

    private final ProductRepository productRepository;

    @Override
    public List<Product> getAll() {
        return productRepository.findAllByOrderByFruitTypeAscVarietyAsc();
    }

    @Override
    public Product getById(UUID id) {
        if(id == null)
            throw new IllegalArgumentException("Идентификатор продукта не указан.");

        return productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Товар с идентификатором " + id + " не найден."));
    }
}
