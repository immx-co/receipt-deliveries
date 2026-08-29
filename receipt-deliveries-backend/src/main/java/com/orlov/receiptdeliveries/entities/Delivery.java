package com.orlov.receiptdeliveries.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Модель поставки.
 */
@Entity
@Table(name = "deliveries")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(onlyExplicitlyIncluded = true)
public class Delivery {

    /**
     * Уникальный идентификатор поставки.
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
     * Поставщик, выполнивший поставку.
     */
    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "supplier_id",
            nullable = false
    )
    private Supplier supplier;

    /**
     * Дата и время поставки.
     */
    @Column(
            name = "delivery_at",
            nullable = false
    )
    @ToString.Include
    OffsetDateTime deliveryAt;

    /**
     * Позиции, входящие в поставку.
     */
    @OneToMany(
            mappedBy = "delivery",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<DeliveryItem> items = new ArrayList<>();

    public Delivery(Supplier supplier,
                    OffsetDateTime deliveryAt) {
        this.supplier = supplier;
        this.deliveryAt = deliveryAt;
    }

    public void addItem(Product product,
                        BigDecimal weightKg,
                        BigDecimal pricePerKg) {
        DeliveryItem item = new DeliveryItem(
                this,
                product,
                weightKg,
                pricePerKg);

        items.add(item);
    }
}
