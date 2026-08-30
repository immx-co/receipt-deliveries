package com.orlov.receiptdeliveries.mappers;

import com.orlov.receiptdeliveries.contracts.organization.OrganizationResponse;
import com.orlov.receiptdeliveries.entities.Organization;
import org.springframework.stereotype.Component;

/**
 * Преобразует модель организации в API контракт.
 */
@Component
public class OrganizationMapper {

    /**
     * Преобразуем модель организации в ответ API.
     *
     * @param organization модель организации
     * @return ответ сервиса с информацией об организации.
     */
    public OrganizationResponse toResponse(Organization organization) {
        return new OrganizationResponse(
                organization.getId(),
                organization.getName(),
                organization.getRole());
    }
}
