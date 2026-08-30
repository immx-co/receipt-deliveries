package com.orlov.receiptdeliveries.services.organization;

import com.orlov.receiptdeliveries.contracts.authorization.OrganizationRole;
import com.orlov.receiptdeliveries.entities.Organization;
import com.orlov.receiptdeliveries.repositories.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Сервис для работы с организациями.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrganizationService implements IOrganizationService {

    private final OrganizationRepository organizationRepository;

    @Override
    public Organization getById(UUID id) {
        if(id == null)
            throw new IllegalArgumentException("Идентификатор организации не указан.");

        return organizationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Организация с идентификатором " + id + " не найдена."));
    }

    @Override
    public Organization getByLogin(String login) {
        if(login == null || login.isBlank())
            throw new IllegalArgumentException("Логин организации не указан.");

        String normalizedLogin = login.trim();

        return organizationRepository.findByLoginIgnoreCase(normalizedLogin)
                .orElseThrow(() -> new NoSuchElementException("Организация с логином " + login + " не найдена."));
    }

    @Override
    public Organization getByIdAndRole(UUID id,
                                       OrganizationRole role) {
        if(id == null)
            throw new IllegalArgumentException("Идентификатор организации не указан.");

        if(role == null)
            throw new IllegalArgumentException("Роль организации не указана.");

        return organizationRepository.findByIdAndRole(
                        id,
                        role)
                .orElseThrow(() -> new NoSuchElementException(
                        "Организация с идентификатором " + id + " и ролью " + role + " не найдена."));
    }

    @Override
    public List<Organization> getAllByRole(OrganizationRole role) {
        if(role == null)
            throw new IllegalArgumentException("Роль организаций не указана.");

        return organizationRepository.findAllByRoleOrderByName(role);
    }
}
