package com.orlov.receiptdeliveries.controllers;

import com.orlov.receiptdeliveries.contracts.delivery.CreateDeliveryRequest;
import com.orlov.receiptdeliveries.contracts.delivery.DeliveryReportResponse;
import com.orlov.receiptdeliveries.contracts.delivery.DeliveryResponse;
import com.orlov.receiptdeliveries.entities.Delivery;
import com.orlov.receiptdeliveries.mappers.DeliveryMapper;
import com.orlov.receiptdeliveries.services.delivery.IDeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Контроллер поставок.
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class DeliveryController {

    private final IDeliveryService deliveryService;

    private final DeliveryMapper deliveryMapper;

    /**
     * Регистрирует поступившую поставку.
     *
     * @param receiverId            идентификатор приемщика
     * @param createDeliveryRequest запрос на регистрацию поступившей поставки
     * @return ответ сервиса с информацией о зарегистрированной поставке.
     */
    @PostMapping("/receivers/{receiverId}/deliveries")
    @ResponseStatus(HttpStatus.CREATED)
    public DeliveryResponse register(@PathVariable UUID receiverId,
                                     @RequestBody CreateDeliveryRequest createDeliveryRequest) {
        Delivery delivery = deliveryService.register(
                receiverId,
                createDeliveryRequest);

        return deliveryMapper.toResponse(delivery);
    }

    /**
     * Возвращает историю поставщика.
     *
     * @param supplierId идентификатор поставщика
     * @return история поставок конкретного поставщика
     */
    @GetMapping("/suppliers/{supplierId}/deliveries")
    public List<DeliveryResponse> getAllForSupplier(@PathVariable UUID supplierId) {
        return deliveryService.getAllForSupplier(supplierId)
                .stream()
                .map(deliveryMapper::toResponse)
                .toList();
    }

    /**
     * Возвращает историю поставок приемщика.
     *
     * @param receiverId идентификатор приемщика
     * @return история поставок конкретного приемщика
     */
    @GetMapping("/receivers/{receiverId}/deliveries")
    public List<DeliveryResponse> getAllForReceiver(@PathVariable UUID receiverId) {
        return deliveryService.getAllForReceiver(receiverId)
                .stream()
                .map(deliveryMapper::toResponse)
                .toList();
    }

    /**
     * Возвращает поставку по ее идентификатору.
     *
     * @param organizationId идентификатор организации
     * @param deliveryId     идентификатор поставки
     * @return ответ сервиса с информацией о поставке.
     */
    @GetMapping("/organizations/{organizationId}/deliveries/{deliveryId}")
    public DeliveryResponse getById(@PathVariable UUID organizationId,
                                    @PathVariable UUID deliveryId) {
        Delivery delivery = deliveryService.getByIdForOrganization(
                deliveryId,
                organizationId);

        return deliveryMapper.toResponse(delivery);
    }

    /**
     * Формирует отчет приемщика за выбранный период.
     *
     * @param receiverId идентификатор приемщика
     * @param from       начало периода включительно
     * @param to         конец периода
     * @return сформированный отчет о поставках приемщика
     */
    @GetMapping("/receivers/{receiverId}/deliveries/report")
    public DeliveryReportResponse getReport(@PathVariable UUID receiverId,
                                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
                                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to) {
        return deliveryService.getReport(
                receiverId,
                from,
                to);
    }
}
