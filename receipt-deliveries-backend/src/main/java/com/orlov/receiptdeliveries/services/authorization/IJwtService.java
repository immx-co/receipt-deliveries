package com.orlov.receiptdeliveries.services.authorization;

import com.orlov.receiptdeliveries.entities.Organization;

import java.time.Instant;

/**
 * Интерфейс сервиса для создания и хранения JWT токена.
 */
public interface IJwtService {

    /**
     * Создает JWT токен для указанной организации.
     *
     * @param organization авторизованная организация
     * @return созданный JWT токен.
     */
    GeneratedToken generateAccessToken(Organization organization);

    /**
     * Внутренний результат генерации JWT токена.
     *
     * @param value     строковое значение токена
     * @param expiresAt время окончания токена
     */
    record GeneratedToken(String value,
                          Instant expiresAt) {

    }
}
