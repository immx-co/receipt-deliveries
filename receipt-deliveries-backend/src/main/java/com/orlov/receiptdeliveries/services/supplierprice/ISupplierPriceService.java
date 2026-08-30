package com.orlov.receiptdeliveries.services.supplierprice;

import com.orlov.receiptdeliveries.entities.SupplierPrice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Интерфейс сервиса для работы с ценами поставщика.
 */
public interface ISupplierPriceService {

    List<SupplierPrice> getAllForSupplier(UUID supplierId);

    List<SupplierPrice> getActiveForSupplier(UUID supplierId,
                                             LocalDate priceDate);

    SupplierPrice create(UUID supplierId,
                        UUID productId,
                        BigDecimal pricePerKg,
                        LocalDate startDate,
                        LocalDate endDate);

    SupplierPrice update(UUID priceId,
                         UUID supplierId,
                         UUID productId,
                         BigDecimal pricePerKg,
                         LocalDate startDate,
                         LocalDate endDate);

    void delete(UUID priceId,
                UUID supplierId);
}
