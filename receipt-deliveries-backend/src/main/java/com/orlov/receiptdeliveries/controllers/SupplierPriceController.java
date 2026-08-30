package com.orlov.receiptdeliveries.controllers;

import com.orlov.receiptdeliveries.contracts.supplierprice.SaveSupplierPriceRequest;
import com.orlov.receiptdeliveries.contracts.supplierprice.SupplierPriceResponse;
import com.orlov.receiptdeliveries.entities.SupplierPrice;
import com.orlov.receiptdeliveries.mappers.SupplierPriceMapper;
import com.orlov.receiptdeliveries.services.supplierprice.ISupplierPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/suppliers/{supplierId}/prices")
@RequiredArgsConstructor
public class SupplierPriceController {

    private final ISupplierPriceService supplierPriceService;

    private final SupplierPriceMapper supplierPriceMapper;

    /**
     * Возвращает всю историю цен поставщика.
     *
     * @param supplierId идентификатор поставщика
     * @return история цен поставщика.
     */
    @GetMapping
    public List<SupplierPriceResponse> getAll(@PathVariable UUID supplierId) {
        return supplierPriceService.getAllForSupplier(supplierId)
                .stream()
                .map(supplierPriceMapper::toResponse)
                .toList();
    }

    /**
     * Возвращает цены, действующие на выбранную дату.
     *
     * @param supplierId идентификатор поставщика
     * @param date       дата действия цены
     * @return действующие цены
     */
    @GetMapping("/active")
    public List<SupplierPriceResponse> getActive(@PathVariable UUID supplierId,
                                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return supplierPriceService.getActiveForSupplier(
                        supplierId,
                        date)
                .stream()
                .map(supplierPriceMapper::toResponse)
                .toList();
    }

    /**
     * Задает новую цену товара.
     *
     * @param supplierId               идентификатор поставщика, который задает новую цену товара
     * @param saveSupplierPriceRequest запрос на создание новой цены товара
     * @return ответ сервиса с информацией о сохраненной цене товара.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SupplierPriceResponse create(@PathVariable UUID supplierId,
                                        @RequestBody SaveSupplierPriceRequest saveSupplierPriceRequest) {
        SupplierPrice supplierPrice = supplierPriceService.create(
                supplierId,
                saveSupplierPriceRequest.productId(),
                saveSupplierPriceRequest.pricePerKg(),
                saveSupplierPriceRequest.startDate(),
                saveSupplierPriceRequest.endDate());

        return supplierPriceMapper.toResponse(supplierPrice);
    }

    /**
     * Изменяет существующую цену товара.
     *
     * @param supplierId               идентификатор поставщика, который изменяет цену товара
     * @param priceId                  идентификатор товара
     * @param saveSupplierPriceRequest запрос на изменение цены товара
     * @return ответ сервиса с информацией об измененной цене товара
     */
    @PutMapping("/{priceId}")
    public SupplierPriceResponse update(@PathVariable UUID supplierId,
                                        @PathVariable UUID priceId,
                                        @RequestBody SaveSupplierPriceRequest saveSupplierPriceRequest) {
        SupplierPrice supplierPrice = supplierPriceService.update(
                priceId,
                supplierId,
                saveSupplierPriceRequest.productId(),
                saveSupplierPriceRequest.pricePerKg(),
                saveSupplierPriceRequest.startDate(),
                saveSupplierPriceRequest.endDate());

        return supplierPriceMapper.toResponse(supplierPrice);
    }

    /**
     * Удаляет цену товара.
     *
     * @param supplierId идентификатор поставщика, который удаляет цену товара
     * @param priceId    идентификатор товара, у которого удалится цена
     */
    @DeleteMapping("/{priceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID supplierId,
                       @PathVariable UUID priceId) {
        supplierPriceService.delete(
                priceId,
                supplierId);
    }
}
