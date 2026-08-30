package com.orlov.receiptdeliveries.contracts.authorization;

import jakarta.validation.constraints.NotBlank;

/**
 * Запрос на авторизацию организации.
 * @param login логин организации
 * @param password пароль организации
 */
public record LoginRequest(
        @NotBlank(message = "Логин не указан.") String login,
        @NotBlank(message = "Пароль не указан.") String password
) {
}
