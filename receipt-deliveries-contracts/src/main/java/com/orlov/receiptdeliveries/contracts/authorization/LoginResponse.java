package com.orlov.receiptdeliveries.contracts.authorization;

import java.time.Instant;
import java.util.UUID;

/**
 * Результат успешной авторизации.
 *
 * @param accessToken      токен доступа для последующих запросов
 * @param tokenType        тип токена
 * @param expiresAt        время окончания действия токена
 * @param organizationId   идентификатор организации
 * @param organizationName название организации
 * @param login            логин организации
 * @param role             роль организации
 */
public record LoginResponse(String accessToken,
                            String tokenType,
                            Instant expiresAt,
                            UUID organizationId,
                            String organizationName,
                            String login,
                            OrganizationRole role) {
}
