package com.orlov.receiptdeliveries.entities;

import com.orlov.receiptdeliveries.contracts.authorization.OrganizationRole;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Модель организации, которая занимается поставками или приемкой.
 */
@Entity
@Table(name = "organizations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(onlyExplicitlyIncluded = true)
public class Organization {

    /**
     * Уникальный идентификатор организации.
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
     * Название организации.
     */
    @Column(
            name = "name",
            nullable = false,
            length = 100,
            unique = true
    )
    @ToString.Include
    private String name;

    /**
     * Логин организации.
     */
    @Column(
            name = "login",
            nullable = false,
            length = 50
    )
    @ToString.Include
    private String login;

    /**
     * Захешированный пароль организации.
     */
    @Column(
            name = "password_hash",
            nullable = false,
            length = 255
    )
    private String passwordHash;

    /**
     * Роль организации.
     */
    @Enumerated(EnumType.STRING)
    @Column(
            name = "role",
            nullable = false,
            length = 20
    )
    @ToString.Include
    private OrganizationRole role;

    public Organization(String name,
                        String login,
                        String passwordHash,
                        OrganizationRole role) {
        this.name = name;
        this.login = login;
        this.passwordHash = passwordHash;
        this.role = role;
    }
}
