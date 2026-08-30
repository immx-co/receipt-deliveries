package com.orlov.receiptdeliveries.controllers;

import com.orlov.receiptdeliveries.contracts.authorization.OrganizationRole;
import com.orlov.receiptdeliveries.contracts.organization.OrganizationResponse;
import com.orlov.receiptdeliveries.mappers.OrganizationMapper;
import com.orlov.receiptdeliveries.services.organization.IOrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Контроллер организаций.
 */
@RestController
@RequestMapping("/api/v1/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final IOrganizationService organizationService;

    private final OrganizationMapper organizationMapper;

    /**
     * Возвращает организации указанной роли.
     *
     * @param role роль организаций
     * @return список организаций.
     */
    @GetMapping
    public List<OrganizationResponse> getAllByRole(@RequestParam OrganizationRole role) {
        return organizationService.getAllByRole(role)
                .stream()
                .map(organizationMapper::toResponse)
                .toList();
    }
}
