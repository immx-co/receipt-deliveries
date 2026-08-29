package com.orlov.receiptdeliveries.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Модель позиции поставки.
 */
@Entity
@Table(name = "delivery_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(onlyExplicitlyIncluded = true)
public class DeliveryItem {

    /**
     * Уникальный идентификатор позиции поставки.
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
     * Поставка, в которую входит позиция.
     */
    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "delivery_id",
            nullable = false
    )
    private Delivery delivery;

    /**
     * Поставленный товар.
     */
    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "product_id",
            nullable = false
    )
    private Product product;

    /**
     * Вес товара.
     */
    @Column(
            name = "weight_kg",
            nullable = false,
            precision = 12,
            scale = 3
    )
    @ToString.Include
    private BigDecimal weightKg;

    /**
     * Цена килограмма товара на момент поставки.
     */
    @Column(
            name = "price_per_kg",
            nullable = false,
            precision = 12,
            scale = 2
    )
    @ToString.Include
    private BigDecimal pricePerKg;

    DeliveryItem(Delivery delivery,
                 Product product,
                 BigDecimal weightKg,
                 BigDecimal pricePerKg) {
        this.delivery = delivery;
        this.product = product;
        this.weightKg = weightKg;
        this.pricePerKg = pricePerKg;
    }
}
