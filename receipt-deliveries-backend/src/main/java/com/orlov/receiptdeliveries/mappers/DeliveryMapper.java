package com.orlov.receiptdeliveries.mappers;

import com.orlov.receiptdeliveries.contracts.delivery.DeliveryResponse;
import com.orlov.receiptdeliveries.contracts.deliveryitem.DeliveryItemResponse;
import com.orlov.receiptdeliveries.entities.Delivery;
import com.orlov.receiptdeliveries.entities.DeliveryItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Преобразует модель поставок в API контракт.
 */
@Component
@RequiredArgsConstructor
public class DeliveryMapper {

    /**
     * Mapper организаций.
     */
    private final OrganizationMapper organizationMapper;

    /**
     * Mapper продуктов.
     */
    private final ProductMapper productMapper;

    /**
     * Преобразует поставку в ответ API.
     *
     * @param delivery поставка
     * @return информация о поставке
     */
    public DeliveryResponse toResponse(Delivery delivery) {
        List<DeliveryItemResponse> items = delivery.getItems()
                .stream()
                .map(this::toItemResponse)
                .toList();

        BigDecimal totalWeightKg = items.stream()
                .map(DeliveryItemResponse::weightKg)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add);

        BigDecimal totalCost = items.stream()
                .map(DeliveryItemResponse::totalCost)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add);

        return new DeliveryResponse(
                delivery.getId(),
                organizationMapper.toResponse(delivery.getSupplier()),
                organizationMapper.toResponse(delivery.getReceiver()),
                delivery.getDeliveryAt(),
                items,
                totalWeightKg,
                totalCost);
    }

    /**
     * Преобразует позицию поставки в ответ API.
     *
     * @param item позиция поставки
     * @return информация о позиции
     */
    private DeliveryItemResponse toItemResponse(DeliveryItem item) {

        BigDecimal totalCost = item.getWeightKg()
                .multiply(item.getPricePerKg());

        return new DeliveryItemResponse(
                item.getId(),
                productMapper.toResponse(item.getProduct()),
                item.getWeightKg(),
                item.getPricePerKg(),
                totalCost);
    }
}
