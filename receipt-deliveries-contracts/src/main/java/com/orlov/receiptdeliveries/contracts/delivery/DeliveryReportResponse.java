package com.orlov.receiptdeliveries.contracts.delivery;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Отчет по поставкам приемщика за выбранный период.
 *
 * @param from          начало периода включительно
 * @param to            конец периода
 * @param items         сгруппированные строки отчета
 * @param deliveryCount количество поставок
 * @param totalWeightKg общий вес всех поставок
 * @param totalCost     общая стоимость всех поставок
 */
public record DeliveryReportResponse(OffsetDateTime from,
                                     OffsetDateTime to,
                                     List<DeliveryReportItemResponse> items,
                                     long deliveryCount,
                                     BigDecimal totalWeightKg,
                                     BigDecimal totalCost) {
}
