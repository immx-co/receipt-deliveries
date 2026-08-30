package com.orlov.receiptdeliveries.repositories;

import com.orlov.receiptdeliveries.entities.Delivery;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы с поставками.
 */
public interface DeliveryRepository extends JpaRepository<Delivery, UUID> {

    /**
     * Ищет все поставки указанного поставщика.
     * @param supplierId идентификатор поставщика, поставки которого следует найти
     * @return список поставок указанного поставщика
     */
    @EntityGraph(attributePaths = {"receiver", "items", "items.product"})
    @Query("""
           SELECT DISTINCT delivery
           FROM Delivery delivery
           WHERE delivery.supplier.id = :supplierId
           ORDER BY delivery.deliveryAt DESC
           """)
    List<Delivery> findAllForSupplier(@Param("supplierId") UUID supplierId);

    /**
     * Ищет все поставки для указанного приемщика.
     * @param receiverId идентификатор приемщика, который зарегистрировал поставки
     * @return список поставок, зарегистрированные указанным приемщиком
     */
    @EntityGraph(attributePaths = {"supplier", "items", "items.product"})
    @Query("""
           SELECT DISTINCT delivery
           FROM Delivery delivery
           WHERE delivery.receiver.id = :receiverId
           ORDER BY delivery.deliveryAt DESC
           """)
    List<Delivery> findAllForReceiver(@Param("receiverId") UUID receiverId);

    /**
     * Ищет поставки, зарегистрированные приемщиком за определенный период.
     * @param receiverId идентификатор приемщика
     * @param from начало периода поиска поставок включительно
     * @param to конец периода поиска поставок
     * @return список поставок, зарегистрированные приемщиком, за определенный период
     */
    @EntityGraph(attributePaths = {"supplier", "items", "items.product"})
    @Query("""
           SELECT DISTINCT delivery
           FROM Delivery delivery
           WHERE delivery.receiver.id = :receiverId
                      AND delivery.deliveryAt >= :from
                      AND delivery.deliveryAt < :to
           ORDER BY delivery.deliveryAt
           """)
    List<Delivery> findAllForReceiverAndPeriod(@Param("receiverId") UUID receiverId,
                                               @Param("from") OffsetDateTime from,
                                               @Param("to") OffsetDateTime to);

    /**
     * Ищет поставку по идентификатору вместе с ее организациями, позициями и продуктами.
     * @param id идентификатор поставки
     * @return найденная поставка
     */
    @Override
    @EntityGraph(attributePaths = {"supplier", "receiver", "items", "items.product"})
    Optional<Delivery> findById(@NonNull UUID id);
}
