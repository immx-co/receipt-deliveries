package com.orlov.receiptdeliveries.services.authorization;

import com.orlov.receiptdeliveries.contracts.authorization.LoginRequest;
import com.orlov.receiptdeliveries.contracts.authorization.LoginResponse;

/**
 * Интерфейс сервиса аутентификаций организаций.
 */
public interface IAuthenticationService {

    /**
     * Проверяет логин и пароль организации и создает токен.
     *
     * @param loginRequest запрос с данными для входа
     * @return результат успешной авторизации.
     */
    LoginResponse login(LoginRequest loginRequest);
}
