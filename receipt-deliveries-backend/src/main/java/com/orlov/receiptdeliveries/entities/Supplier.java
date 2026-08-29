package com.orlov.receiptdeliveries.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Модель поставщика.
 */
@Entity
@Table(name = "suppliers")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(onlyExplicitlyIncluded = true)
public class Supplier {

    /**
     * Уникальный идентификатор поставщика.
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
     * Имя поставщика.
     */
    @Column(
            name = "name",
            nullable = false,
            length = 30,
            unique = true
    )
    @ToString.Include
    private String name;
}
