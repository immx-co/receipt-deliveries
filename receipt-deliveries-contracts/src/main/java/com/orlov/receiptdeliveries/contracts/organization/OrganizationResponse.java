package com.orlov.receiptdeliveries.contracts.organization;

import com.orlov.receiptdeliveries.contracts.authorization.OrganizationRole;

import java.util.UUID;

/**
 * Ответ сервиса с информацией об организации.
 *
 * @param id   идентификатор организации
 * @param name название организации
 * @param role роль организации
 */
public record OrganizationResponse(UUID id,
                                   String name,
                                   OrganizationRole role) {
}
