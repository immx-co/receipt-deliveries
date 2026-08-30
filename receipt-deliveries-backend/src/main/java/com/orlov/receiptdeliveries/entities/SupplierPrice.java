package com.orlov.receiptdeliveries.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Модель записи цены товара.
 */
@Entity
@Table(name = "supplier_prices")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(onlyExplicitlyIncluded = true)
public class SupplierPrice {

    /**
     * Идентификатор записи цены товара.
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
     * Поставщик, назначивший цену товара.
     */
    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "supplier_id",
            nullable = false
    )
    private Organization supplier;

    /**
     * Товар, для которого назначена цена.
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
     * Цена за килограмм товара.
     */
    @Column(
            name = "price_per_kg",
            nullable = false,
            precision = 12,
            scale = 2
    )
    @ToString.Include
    private BigDecimal pricePerKg;

    /**
     * Первый день действия цены товара.
     */
    @Column(
            name = "start_price_effect",
            nullable = false
    )
    @ToString.Include
    private LocalDate startPriceEffect;

    /**
     * Последний день действия цены товара.
     */
    @Column(
            name = "end_price_effect",
            nullable = false
    )
    @ToString.Include
    private LocalDate endPriceEffect;

    public SupplierPrice(Organization supplier, Product product, LocalDate startPriceEffect, BigDecimal pricePerKg, LocalDate endPriceEffect) {
        this.supplier = supplier;
        this.product = product;
        this.startPriceEffect = startPriceEffect;
        this.pricePerKg = pricePerKg;
        this.endPriceEffect = endPriceEffect;
    }
}
