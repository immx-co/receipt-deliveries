package com.orlov.receiptdeliveries.services.delivery;

import com.orlov.receiptdeliveries.contracts.delivery.CreateDeliveryRequest;
import com.orlov.receiptdeliveries.contracts.delivery.DeliveryReportResponse;
import com.orlov.receiptdeliveries.entities.Delivery;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Интерфейс сервиса для работы с поставками.
 */
public interface IDeliveryService {

    /**
     * Регистрирует фактически поступившую поставку.
     *
     * @param receiverId            идентификатор приемщика
     * @param createDeliveryRequest запрос на регистрацию поставки
     * @return зарегистрированная поставка.
     */
    Delivery register(UUID receiverId,
                      CreateDeliveryRequest createDeliveryRequest);

    /**
     * Возвращает историю поставок указанного поставщика.
     *
     * @param supplierId идентификатор поставщика
     * @return поставки указанного поставщика.
     */
    List<Delivery> getAllForSupplier(UUID supplierId);

    /**
     * Возвращает поставки, зарегистрированные приемщиком.
     *
     * @param receiverId идентификатор приемщика
     * @return поставки указанного приемщика.
     */
    List<Delivery> getAllForReceiver(UUID receiverId);

    /**
     * Получает поставку с проверкой доступа организации.
     *
     * @param deliveryId     идентификатор поставки
     * @param organizationId идентификатор организации
     * @return найденная поставка.
     */
    Delivery getByIdForOrganization(UUID deliveryId,
                                    UUID organizationId);

    /**
     * Формирует отчет приемщика за выбранный период.
     *
     * @param receiverId идентификатор приемщика
     * @param from       начало периода включительно
     * @param to         конец периода
     * @return сформированный отчет.
     */
    DeliveryReportResponse getReport(UUID receiverId,
                                     OffsetDateTime from,
                                     OffsetDateTime to);
}
