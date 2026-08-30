package com.orlov.receiptdeliveries.repositories;

import com.orlov.receiptdeliveries.contracts.authorization.OrganizationRole;
import com.orlov.receiptdeliveries.entities.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы с организациями.
 */
public interface OrganizationRepository extends JpaRepository<Organization, UUID> {

    /**
     * Ищет организацию по логину организации.
     *
     * @param login логин организации
     * @return организация, если она найдена по логину.
     */
    Optional<Organization> findByLoginIgnoreCase(String login);

    /**
     * Ищет организации с определенной ролью.
     *
     * @param role роль, организации которой найти
     * @return список найденных организаций, соответствующих определенной роли.
     */
    List<Organization> findAllByRoleOrderByName(OrganizationRole role);

    /**
     * Ищет организацию с определенным идентификатором соответствующую определенной роли.
     *
     * @param id   идентификатор организации
     * @param role роль, организацию которой следует найти
     * @return организация, если она найдена и имеет указанную ролью
     */
    Optional<Organization> findByIdAndRole(UUID id,
                                           OrganizationRole role);
}
