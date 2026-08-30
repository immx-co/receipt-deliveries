package com.orlov.receiptdeliveries.repositories;

import com.orlov.receiptdeliveries.entities.SupplierPrice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Репозиторий для работы с записями цен на товары.
 */
public interface SupplierPriceRepository extends JpaRepository<SupplierPrice, UUID> {

    /**
     * Ищет все цены по идентификатору поставщика.
     * @param supplierId идентификатор поставщика, все цены которого следует найти
     * @return список цен определенного поставщика
     */
    @EntityGraph(attributePaths = "product")
    List<SupplierPrice> findAllBySupplier_IdOrderByStartPriceEffectDesc(UUID supplierId);

    /**
     * Ищет все цены поставщика за указанную дату.
     * @param supplierId идентификатор поставщика
     * @param priceDate дата, цены за которую следует найти
     * @return отфильтрованный по дате список цен поставщика
     */
    @EntityGraph(attributePaths = "product")
    @Query("""
          SELECT supplierPrice
          FROM SupplierPrice supplierPrice
          WHERE supplierPrice.supplier.id = :supplierId
                    AND supplierPrice.startPriceEffect <= :priceDate
                    AND supplierPrice.endPriceEffect >= :priceDate
          ORDER BY supplierPrice.product.fruitType, supplierPrice.product.variety
          """)
    List<SupplierPrice> findAllActiveBySupplierAndDate(@Param("supplierId") UUID supplierId,
                                                       @Param("priceDate") LocalDate priceDate);

    /**
     * Проверяет пересечение периодов цен для одного продукта одного поставщика.
     * @param supplierId идентификатор поставщика
     * @param productId идентификатор продукта
     * @param startDate начало проверяемого периода
     * @param endDate конец проверяемого периода
     * @param excludedPriceId идентификатор исключаемой записи
     * @return количество записей с пересекающимися периодами
     */
    @Query("""
           SELECT COUNT(supplierPrice)
           FROM SupplierPrice supplierPrice
           WHERE supplierPrice.supplier.id = :supplierId
                      AND supplierPrice.product.id = :productId
                      AND supplierPrice.startPriceEffect <= :endDate
                      AND supplierPrice.endPriceEffect >= :startDate
                      AND (:excludedPriceId IS NULL OR supplierPrice.id <> :excludedPriceId)
           """)
    long countOverlappingPeriods(@Param("supplierId") UUID supplierId,
                                 @Param("productId") UUID productId,
                                 @Param("startDate") LocalDate startDate,
                                 @Param("endDate") LocalDate endDate,
                                 @Param("excludedPriceId") UUID excludedPriceId);
}
