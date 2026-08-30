package com.orlov.receiptdeliveries.services.organization;

import com.orlov.receiptdeliveries.contracts.authorization.OrganizationRole;
import com.orlov.receiptdeliveries.entities.Organization;

import java.util.List;
import java.util.UUID;

/**
 * Интерфейс сервиса для работы с организациями.
 */
public interface IOrganizationService {

    /**
     * Получает организацию по идентификатору.
     *
     * @param id идентификатор организации
     * @return найденная организация.
     */
    Organization getById(UUID id);

    /**
     * Получает организацию по логину.
     *
     * @param login логин организации
     * @return найденная организация по логину.
     */
    Organization getByLogin(String login);

    /**
     * Получает организацию по идентификатору, если она соответствует указанной роли.
     *
     * @param id   идентификатор организации
     * @param role ожидаемая роль организации
     * @return найденная организация, если она соответствует указанной роли.
     */
    Organization getByIdAndRole(UUID id,
                                OrganizationRole role);

    /**
     * Возвращает организации, соответствующие указанной роли.
     *
     * @param role ожидаемая роль организаций
     * @return список организацией с указанной ролью.
     */
    List<Organization> getAllByRole(OrganizationRole role);
}
