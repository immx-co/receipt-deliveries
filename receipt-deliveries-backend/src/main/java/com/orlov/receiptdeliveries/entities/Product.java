package com.orlov.receiptdeliveries.entities;

import com.orlov.receiptdeliveries.contracts.product.FruitType;
import com.orlov.receiptdeliveries.contracts.product.FruitVariety;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Модель товара.
 */
@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(onlyExplicitlyIncluded = true)
public class Product {

    /**
     * Уникальный идентификатор товара.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(
            name = "id",
            nullable = false,
            updatable = false
    )
    @ToString.Include
    private UUID id;

    /**
     * Тип фрукта.
     */
    @Enumerated(EnumType.STRING)
    @Column(
            name = "fruit_type",
            nullable = false,
            length = 20
    )
    @ToString.Include
    private FruitType fruitType;

    /**
     * Сорт типа фрукта.
     */
    @Enumerated(EnumType.STRING)
    @Column(
            name = "variety",
            nullable = false,
            length = 50
    )
    @ToString.Include
    private FruitVariety variety;

    public Product(FruitVariety variety) {
        this.variety = variety;
        this.fruitType = variety.getFruitType();
    }
}
