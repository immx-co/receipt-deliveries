package com.orlov.receiptdeliveries.controllers;

import com.orlov.receiptdeliveries.contracts.product.ProductResponse;
import com.orlov.receiptdeliveries.mappers.ProductMapper;
import com.orlov.receiptdeliveries.services.product.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Контроллер товаров.
 */
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final IProductService productService;

    private final ProductMapper productMapper;

    /**
     * Возвращает все доступные товары.
     * @return список товаров.
     */
    @GetMapping
    public List<ProductResponse> getAll() {
        return productService.getAll()
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }
}
