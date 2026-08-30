package com.orlov.receiptdeliveries.services.supplierprice;

import com.orlov.receiptdeliveries.contracts.authorization.OrganizationRole;
import com.orlov.receiptdeliveries.entities.Organization;
import com.orlov.receiptdeliveries.entities.Product;
import com.orlov.receiptdeliveries.entities.SupplierPrice;
import com.orlov.receiptdeliveries.repositories.SupplierPriceRepository;
import com.orlov.receiptdeliveries.services.organization.IOrganizationService;
import com.orlov.receiptdeliveries.services.product.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Сервис для работы с ценами поставщика.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SupplierPriceService implements ISupplierPriceService {

    private final SupplierPriceRepository supplierPriceRepository;

    private final IOrganizationService organizationService;

    private final IProductService productService;

    @Override
    public List<SupplierPrice> getAllForSupplier(UUID supplierId) {
        organizationService.getByIdAndRole(
                supplierId,
                OrganizationRole.SUPPLIER);

        return supplierPriceRepository.findAllBySupplier_IdOrderByStartPriceEffectDesc(supplierId);
    }

    @Override
    public List<SupplierPrice> getActiveForSupplier(UUID supplierId,
                                                    LocalDate priceDate) {
        organizationService.getByIdAndRole(
                supplierId,
                OrganizationRole.SUPPLIER);

        if(priceDate == null)
            throw new IllegalArgumentException("Дата действия цены не указана.");

        return supplierPriceRepository.findAllActiveBySupplierAndDate(
                supplierId,
                priceDate);
    }

    @Override
    @Transactional
    public SupplierPrice create(UUID supplierId,
                                UUID productId,
                                BigDecimal pricePerKg,
                                LocalDate startDate,
                                LocalDate endDate) {
        validatePriceAndPeriod(
                pricePerKg,
                startDate,
                endDate);

        Organization supplier = organizationService.getByIdAndRole(
                supplierId,
                OrganizationRole.SUPPLIER);

        Product product = productService.getById(productId);

        validateNoOverlappingPeriods(
                supplierId,
                productId,
                startDate,
                endDate,
                null);

        SupplierPrice supplierPrice = new SupplierPrice(
                supplier,
                product,
                startDate,
                pricePerKg,
                endDate);

        return supplierPriceRepository.save(supplierPrice);
    }

    @Override
    @Transactional
    public SupplierPrice update(UUID priceId,
                                UUID supplierId,
                                UUID productId,
                                BigDecimal pricePerKg,
                                LocalDate startDate,
                                LocalDate endDate) {
        validatePriceAndPeriod(
                pricePerKg,
                startDate,
                endDate);

        SupplierPrice supplierPrice = getPriceOwnedBySupplier(
                priceId,
                supplierId);

        Organization supplier = organizationService.getByIdAndRole(
                supplierId,
                OrganizationRole.SUPPLIER);

        Product product = productService.getById(productId);

        validateNoOverlappingPeriods(
                supplierId,
                productId,
                startDate,
                endDate,
                priceId);

        supplierPrice.setSupplier(supplier);
        supplierPrice.setProduct(product);
        supplierPrice.setPricePerKg(pricePerKg);
        supplierPrice.setStartPriceEffect(startDate);
        supplierPrice.setEndPriceEffect(endDate);

        return supplierPriceRepository.save(supplierPrice);
    }

    @Override
    @Transactional
    public void delete(UUID priceId,
                       UUID supplierId) {
        SupplierPrice supplierPrice = getPriceOwnedBySupplier(
                priceId,
                supplierId);

        supplierPriceRepository.delete(supplierPrice);
    }

    private SupplierPrice getPriceOwnedBySupplier(UUID priceId,
                                                  UUID supplierId) {
        if(priceId == null)
            throw new IllegalArgumentException("Идентификатор цены не указан.");

        if(supplierId == null)
            throw new IllegalArgumentException("Идентификатор поставщика не указан.");

        SupplierPrice supplierPrice = supplierPriceRepository.findById(priceId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Запись цены с идентификатором " + priceId + " не найдена."));

        if(!supplierPrice.getSupplier()
                .getId()
                .equals(supplierId)) {
            throw new SecurityException("Поставщик не может изменять цены другой организации.");
        }

        return supplierPrice;
    }

    private void validatePriceAndPeriod(BigDecimal pricePerKg,
                                        LocalDate startDate,
                                        LocalDate endDate) {
        if(pricePerKg == null || pricePerKg.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Цена за килограмм должна быть указана и больше нуля.");

        if(startDate == null || endDate == null)
            throw new IllegalArgumentException("Период действия цены не указан.");

        if(startDate.isAfter(endDate))
            throw new IllegalArgumentException("Начало периода действия цены не может быть позже конца.");
    }

    private void validateNoOverlappingPeriods(UUID supplierId,
                                              UUID productId,
                                              LocalDate startDate,
                                              LocalDate endDate,
                                              UUID excludedPriceId) {
        long overlappingPeriods = supplierPriceRepository.countOverlappingPeriods(
                supplierId,
                productId,
                startDate,
                endDate,
                excludedPriceId);

        if(overlappingPeriods > 0)
            throw new IllegalStateException("Для выбранного периода уже существует цена с пересекающимся периодом "
                                            + "действия.");
    }
}
