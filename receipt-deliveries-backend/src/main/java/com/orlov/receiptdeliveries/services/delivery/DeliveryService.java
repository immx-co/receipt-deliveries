package com.orlov.receiptdeliveries.services.delivery;

import com.orlov.receiptdeliveries.contracts.authorization.OrganizationRole;
import com.orlov.receiptdeliveries.contracts.delivery.CreateDeliveryItemRequest;
import com.orlov.receiptdeliveries.contracts.delivery.CreateDeliveryRequest;
import com.orlov.receiptdeliveries.contracts.delivery.DeliveryReportItemResponse;
import com.orlov.receiptdeliveries.contracts.delivery.DeliveryReportResponse;
import com.orlov.receiptdeliveries.entities.Delivery;
import com.orlov.receiptdeliveries.entities.DeliveryItem;
import com.orlov.receiptdeliveries.entities.Organization;
import com.orlov.receiptdeliveries.entities.SupplierPrice;
import com.orlov.receiptdeliveries.repositories.DeliveryRepository;
import com.orlov.receiptdeliveries.services.organization.IOrganizationService;
import com.orlov.receiptdeliveries.services.supplierprice.ISupplierPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервис для работы с поставками.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryService implements IDeliveryService {

    private static final ZoneId ZONE = ZoneId.of("Europe/Moscow");

    private final DeliveryRepository deliveryRepository;

    private final IOrganizationService organizationService;

    private final ISupplierPriceService supplierPriceService;

    @Override
    @Transactional
    public Delivery register(UUID receiverId,
                             CreateDeliveryRequest createDeliveryRequest) {
        validateDeliveryData(
                receiverId,
                createDeliveryRequest);

        Organization supplier = organizationService.getByIdAndRole(
                createDeliveryRequest.supplierId(),
                OrganizationRole.SUPPLIER);

        Organization receiver = organizationService.getByIdAndRole(
                receiverId,
                OrganizationRole.RECEIVER);

        LocalDate priceDate = createDeliveryRequest.deliveryAt()
                .atZoneSameInstant(ZONE)
                .toLocalDate();

        List<SupplierPrice> activePrices = supplierPriceService.getActiveForSupplier(
                createDeliveryRequest.supplierId(),
                priceDate);

        Map<UUID, List<SupplierPrice>> pricesByProduct = activePrices.stream()
                .collect(Collectors.groupingBy(price -> price.getProduct()
                        .getId()));

        Delivery delivery = new Delivery(
                supplier,
                receiver,
                createDeliveryRequest.deliveryAt());

        Set<UUID> addedProductIds = new HashSet<>();

        for(CreateDeliveryItemRequest item : createDeliveryRequest.items()) {
            validateDeliveryItem(item);

            if(!addedProductIds.add(item.productId()))
                throw new IllegalArgumentException("Один продукт нельзя добавить в поставку несколько раз.");

            List<SupplierPrice> productPrices = pricesByProduct.get(item.productId());

            if(productPrices == null || productPrices.isEmpty())
                throw new IllegalStateException(
                        "Для продукта " + item.productId() + " отсутствует действующая цена на дату поставки.");

            if(productPrices.size() > 1)
                throw new IllegalStateException(
                        "Для продукта " + item.productId() + " найдено несколько действующих цен.");

            SupplierPrice activePrice = productPrices.getFirst();

            delivery.addItem(
                    activePrice.getProduct(),
                    item.weightKg(),
                    activePrice.getPricePerKg());
        }

        return deliveryRepository.save(delivery);
    }

    @Override
    public List<Delivery> getAllForSupplier(UUID supplierId) {
        organizationService.getByIdAndRole(
                supplierId,
                OrganizationRole.SUPPLIER);

        return deliveryRepository.findAllForSupplier(supplierId);
    }

    @Override
    public List<Delivery> getAllForReceiver(UUID receiverId) {
        organizationService.getByIdAndRole(
                receiverId,
                OrganizationRole.RECEIVER);

        return deliveryRepository.findAllForReceiver(receiverId);
    }

    @Override
    public Delivery getByIdForOrganization(UUID deliveryId,
                                           UUID organizationId) {
        if(deliveryId == null || organizationId == null)
            throw new IllegalArgumentException("Идентификаторы поставки и организации должны быть указаны.");

        organizationService.getById(organizationId);

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Поставка с идентификатором " + deliveryId + " не найдена."));

        boolean supplierHasAccess = delivery.getSupplier()
                .getId()
                .equals(organizationId);

        boolean receiverHasAccess = delivery.getReceiver()
                .getId()
                .equals(organizationId);

        if(!supplierHasAccess && !receiverHasAccess)
            throw new SecurityException("Организация не имеет доступа к этой поставке.");

        return delivery;
    }

    @Override
    public DeliveryReportResponse getReport(UUID receiverId,
                                            OffsetDateTime from,
                                            OffsetDateTime to) {
        organizationService.getByIdAndRole(
                receiverId,
                OrganizationRole.RECEIVER);

        validatePeriod(
                from,
                to);

        List<Delivery> deliveries = deliveryRepository.findAllForReceiverAndPeriod(
                receiverId,
                from,
                to);

        Map<ReportKey, DeliveryReportItemResponse> groupedItems = new HashMap<>();

        for(Delivery delivery : deliveries) {
            for(DeliveryItem item : delivery.getItems()) {
                ReportKey key = new ReportKey(
                        delivery.getSupplier()
                                .getId(),
                        item.getProduct()
                                .getId());

                BigDecimal itemCost = item.getWeightKg()
                        .multiply(item.getPricePerKg());

                DeliveryReportItemResponse reportItem = new DeliveryReportItemResponse(
                        delivery.getSupplier()
                                .getId(),
                        delivery.getSupplier()
                                .getName(),
                        item.getProduct()
                                .getId(),
                        item.getProduct()
                                .getFruitType(),
                        item.getProduct()
                                .getVariety(),
                        item.getWeightKg(),
                        itemCost);

                groupedItems.merge(
                        key,
                        reportItem,
                        DeliveryService::mergeReportItems);
            }
        }

        List<DeliveryReportItemResponse> reportItems = groupedItems.values()
                .stream()
                .sorted(Comparator.comparing(DeliveryReportItemResponse::supplierName)
                        .thenComparing(item -> item.fruitType()
                                .name())
                        .thenComparing(item -> item.variety()
                                .name()))
                .toList();

        BigDecimal totalWeightKg = reportItems.stream()
                .map(DeliveryReportItemResponse::totalWeightKg)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add);

        BigDecimal totalCost = reportItems.stream()
                .map(DeliveryReportItemResponse::totalCost)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add);

        return new DeliveryReportResponse(
                from,
                to,
                reportItems,
                deliveries.size(),
                totalWeightKg,
                totalCost);
    }

    private void validateDeliveryData(UUID receiverId,
                                      CreateDeliveryRequest request) {
        if(receiverId == null)
            throw new IllegalArgumentException("Приёмщик не указан.");

        if(request == null)
            throw new IllegalArgumentException("Данные поставки не указаны.");

        if(request.supplierId() == null)
            throw new IllegalArgumentException("Поставщик не указан.");

        if(receiverId.equals(request.supplierId()))
            throw new IllegalArgumentException("Поставщик и приёмщик не могут совпадать.");

        if(request.deliveryAt() == null)
            throw new IllegalArgumentException("Дата и время поставки не указаны.");

        if(request.items() == null || request.items()
                .isEmpty()) {

            throw new IllegalArgumentException("Поставка должна содержать хотя бы одну позицию.");
        }

        if(request.items()
                   .size() > 4) {
            throw new IllegalArgumentException("Поставка не может содержать больше четырёх продуктов.");
        }
    }

    private void validateDeliveryItem(CreateDeliveryItemRequest item) {
        if(item == null || item.productId() == null)
            throw new IllegalArgumentException("Продукт позиции поставки не указан.");

        if(item.weightKg() == null || item.weightKg()
                                              .compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException("Вес продукта должен быть больше нуля.");
        }
    }

    private void validatePeriod(OffsetDateTime from,
                                OffsetDateTime to) {
        if(from == null || to == null) {
            throw new IllegalArgumentException("Период не указан.");
        }

        if(!from.isBefore(to)) {
            throw new IllegalArgumentException("Начало периода должно быть раньше конца.");
        }
    }

    private static DeliveryReportItemResponse mergeReportItems(DeliveryReportItemResponse current,
                                                               DeliveryReportItemResponse added) {
        return new DeliveryReportItemResponse(
                current.supplierId(),
                current.supplierName(),
                current.productId(),
                current.fruitType(),
                current.variety(),
                current.totalWeightKg()
                        .add(added.totalWeightKg()),
                current.totalCost()
                        .add(added.totalCost()));
    }

    private record ReportKey(UUID supplierId,
                             UUID productId) {

    }
}
