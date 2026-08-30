package com.orlov.receiptdeliveries.frontend.views;

import com.orlov.receiptdeliveries.client.DeliveryClient;
import com.orlov.receiptdeliveries.client.ProductClient;
import com.orlov.receiptdeliveries.client.SupplierPriceClient;
import com.orlov.receiptdeliveries.contracts.authorization.OrganizationRole;
import com.orlov.receiptdeliveries.contracts.delivery.DeliveryResponse;
import com.orlov.receiptdeliveries.contracts.deliveryitem.DeliveryItemResponse;
import com.orlov.receiptdeliveries.contracts.product.ProductResponse;
import com.orlov.receiptdeliveries.contracts.supplierprice.SaveSupplierPriceRequest;
import com.orlov.receiptdeliveries.contracts.supplierprice.SupplierPriceResponse;
import com.orlov.receiptdeliveries.frontend.layouts.MainLayout;
import com.orlov.receiptdeliveries.frontend.session.OrganizationSession;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Route(
        value = SupplierView.ROUTE,
        layout = MainLayout.class
)
@PageTitle("Поставки | Receipt Deliveries")
public class SupplierView extends VerticalLayout implements BeforeEnterObserver {

    public static final String ROUTE = "supplies";

    private static final Locale RUSSIAN_LOCALE = Locale.forLanguageTag("ru-RU");

    private static final DateTimeFormatter DELIVERY_DATE_FORMATTER = DateTimeFormatter.ofPattern(
            "d MMMM yyyy, HH:mm",
            RUSSIAN_LOCALE);

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(
            "d MMMM yyyy",
            RUSSIAN_LOCALE);

    private final DeliveryClient deliveryClient;

    private final SupplierPriceClient supplierPriceClient;

    private final ProductClient productClient;

    private final OrganizationSession organizationSession;

    private final Grid<DeliveryResponse> deliveryGrid = new Grid<>(
            DeliveryResponse.class,
            false);

    private final Grid<SupplierPriceResponse> activePriceGrid = new Grid<>(
            SupplierPriceResponse.class,
            false);

    private final Span activePriceDate = new Span();

    private List<DeliveryResponse> deliveries = List.of();

    public SupplierView(DeliveryClient deliveryClient,
                        SupplierPriceClient supplierPriceClient,
                        ProductClient productClient,
                        OrganizationSession organizationSession) {
        this.deliveryClient = deliveryClient;
        this.supplierPriceClient = supplierPriceClient;
        this.productClient = productClient;
        this.organizationSession = organizationSession;

        configurePage();
        configureDeliveryGrid();
        configureActivePriceGrid();
        createContent();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if(!organizationSession.isAuthenticated() || organizationSession.getRole() != OrganizationRole.SUPPLIER)
            return;

        loadDeliveries();
        loadActivePrices();
    }

    private void configurePage() {
        setWidthFull();
        setMaxWidth("1200px");
        setPadding(true);
        setSpacing(true);

        getStyle().set(
                "margin",
                "0 auto");
    }

    private void createContent() {
        H1 title = new H1("Поставки");

        title.getStyle()
                .set(
                        "margin-bottom",
                        "0");

        com.vaadin.flow.component.button.Button configurePricesButton = new Button("Настроить цены");

        configurePricesButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        configurePricesButton.addClickListener(event -> new PriceManagementDialog().open());

        add(
                title,
                configurePricesButton,
                createDeliveryHistorySection(),
                createActivePricesSection());
    }

    private VerticalLayout createDeliveryHistorySection() {
        H3 title = new H3("История поставок");

        VerticalLayout section = new VerticalLayout(
                title,
                deliveryGrid);

        section.setWidthFull();
        section.setPadding(false);

        return section;
    }

    private VerticalLayout createActivePricesSection() {
        H3 title = new H3("действующие цены");

        HorizontalLayout header = new HorizontalLayout(
                title,
                activePriceDate);

        header.setWidthFull();
        header.setAlignItems(Alignment.CENTER);
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);

        VerticalLayout section = new VerticalLayout(
                header,
                activePriceGrid);

        section.setWidthFull();
        section.setPadding(false);

        return section;
    }

    private void configureDeliveryGrid() {
        deliveryGrid.addColumn(delivery -> delivery.receiver()
                        .name())
                .setHeader("Приемщик")
                .setAutoWidth(true)
                .setFlexGrow(1);

        deliveryGrid.addColumn(delivery -> formatDateTime(delivery.deliveryAt()))
                .setHeader("Дата")
                .setAutoWidth(true);

        deliveryGrid.addColumn(this::formatProducts)
                .setHeader("Продукция")
                .setAutoWidth(true)
                .setFlexGrow(1);

        deliveryGrid.addColumn(delivery -> formatMoney(delivery.totalCost()))
                .setHeader("Стоимост")
                .setAutoWidth(true);

        deliveryGrid.addComponentColumn(delivery -> {
                    com.vaadin.flow.component.button.Button openButton = new com.vaadin.flow.component.button.Button(
                            "Открыть");

                    openButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

                    openButton.addClickListener(event -> openDeliveryDetails(delivery));

                    return openButton;
                })
                .setHeader("")
                .setAutoWidth(true)
                .setFlexGrow(0);

        deliveryGrid.setWidthFull();
        deliveryGrid.setHeight("330px");
    }

    private void configureActivePriceGrid() {
        activePriceGrid.addColumn(price -> formatProduct(price.product()))
                .setHeader("Продукция")
                .setAutoWidth(true)
                .setFlexGrow(1);

        activePriceGrid.addColumn(price -> formatMoney(price.pricePerKg()) + "/кг")
                .setHeader("Цена за кг")
                .setAutoWidth(true);

        activePriceGrid.addColumn(price -> formatDate(price.startDate()))
                .setHeader("Начало периода")
                .setAutoWidth(true);

        activePriceGrid.addColumn(price -> formatDate(price.endDate()))
                .setHeader("Конец периода")
                .setAutoWidth(true);

        activePriceGrid.setWidthFull();
        activePriceGrid.setHeight("270px");
    }

    private void loadDeliveries() {
        UUID supplierId = organizationSession.getOrganizationId();
        String accessToken = organizationSession.getAccessToken();

        try {
            deliveries = deliveryClient.getAllForSupplier(
                            supplierId,
                            accessToken)
                    .stream()
                    .sorted(Comparator.comparing(DeliveryResponse::deliveryAt)
                            .reversed())
                    .toList();

            deliveryGrid.setItems(deliveries);
        } catch(RestClientException ex) {
            showError("Не удалось загрузить историю поставок.");
        }
    }

    private void loadActivePrices() {
        UUID supplierId = organizationSession.getOrganizationId();
        String accessToken = organizationSession.getAccessToken();
        LocalDate currentDate = LocalDate.now();

        activePriceDate.setText("на " + formatDate(currentDate));

        try {
            activePriceGrid.setItems(supplierPriceClient.getActive(
                    supplierId,
                    currentDate,
                    accessToken));
        } catch(RestClientException ex) {
            showError("Не удалось загрузить действующие цены.");
        }
    }

    private void openDeliveryDetails(DeliveryResponse delivery) {
        Dialog dialog = new Dialog();

        dialog.setHeaderTitle("Информация о поставке");
        dialog.setWidth("800px");
        dialog.setMaxWidth("95vw");

        VerticalLayout info = new VerticalLayout(
                new Span("Приемщик " + delivery.receiver()
                        .name()),
                new Span("Дата " + formatDateTime(delivery.deliveryAt())),
                new Span("Общий вес " + formatWeight(delivery.totalWeightKg())),
                new Span("Стоимость " + formatMoney(delivery.totalCost())));

        info.setPadding(true);

        Grid<DeliveryItemResponse> itemGrid = new Grid<>(DeliveryItemResponse.class);

        itemGrid.addColumn(item -> formatProduct(item.product()))
                .setHeader("Продукция")
                .setAutoWidth(true)
                .setFlexGrow(1);

        itemGrid.addColumn(item -> formatWeight(item.weightKg()))
                .setHeader("Вес")
                .setAutoWidth(true);

        itemGrid.addColumn(item -> formatMoney(item.pricePerKg()) + " кг")
                .setHeader("Цена")
                .setAutoWidth(true);

        itemGrid.addColumn(item -> formatMoney(item.totalCost()))
                .setHeader("Стоимость")
                .setAutoWidth(true);

        itemGrid.setItems(delivery.items());
        itemGrid.setWidthFull();
        itemGrid.setHeight("260px");

        com.vaadin.flow.component.button.Button closeButton = new com.vaadin.flow.component.button.Button(
                "Закрыть",
                event -> dialog.close());

        dialog.add(
                info,
                itemGrid);
        dialog.getFooter()
                .add(closeButton);
        dialog.open();
    }

    private String formatProducts(DeliveryResponse delivery) {
        if(delivery.items() == null || delivery.items()
                .isEmpty())
            return "-";

        List<String> products = delivery.items()
                .stream()
                .map(DeliveryItemResponse::product)
                .map(this::formatProduct)
                .distinct()
                .toList();

        if(products.size() <= 2)
            return String.join(
                    ", ",
                    products);

        return products.size() + " вида продукции";
    }

    private String formatProduct(ProductResponse product) {
        if(product == null || product.variety() == null)
            return "-";

        return product.variety()
                .getDisplayName();
    }

    private String formatDateTime(OffsetDateTime dateTime) {
        return dateTime == null ? "-" : DELIVERY_DATE_FORMATTER.format(dateTime);
    }

    private String formatDate(LocalDate date) {
        return date == null ? "-" : DATE_FORMATTER.format(date);
    }

    private String formatWeight(BigDecimal value) {
        return formatNumber(
                value,
                3) + " кг";
    }

    private String formatMoney(BigDecimal value) {
        return formatNumber(
                value,
                2) + " Р";
    }

    private String formatNumber(BigDecimal value,
                                int maxFractionDigits) {
        if(value == null)
            return "0";

        NumberFormat formatter = NumberFormat.getNumberInstance(RUSSIAN_LOCALE);

        formatter.setMinimumFractionDigits(0);
        formatter.setMaximumFractionDigits(maxFractionDigits);

        return formatter.format(value);
    }

    private void showError(String message) {
        Notification notification = Notification.show(
                message,
                5000,
                Notification.Position.MIDDLE);

        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }

    private void showSuccess(String message) {
        Notification notification = Notification.show(
                message,
                3000,
                Notification.Position.BOTTOM_END);

        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private class PriceManagementDialog extends Dialog {

        private final Grid<SupplierPriceResponse> priceGrid = new Grid<>(
                SupplierPriceResponse.class,
                false);

        private final ComboBox<ProductResponse> productField = new ComboBox<>("Продукция");

        private final BigDecimalField priceField = new BigDecimalField("Цена за кг");

        private final DatePicker startDateField = new DatePicker("Начало периода");

        private final DatePicker endDateField = new DatePicker("Конец периода");

        private final com.vaadin.flow.component.button.Button saveButton =
                new com.vaadin.flow.component.button.Button("Добавить цену");

        private SupplierPriceResponse editedPrice;

        PriceManagementDialog() {
            setHeaderTitle("Настройка цен");
            setWidth("950px");
            setMaxWidth("95vw");

            configureFields();
            configureGrid();
            configureButtons();

            FormLayout form = new FormLayout(
                    productField,
                    priceField,
                    startDateField,
                    endDateField);

            form.setAutoResponsive(true);
            form.setColumnWidth("16rem");
            form.setExpandFields(true);

            VerticalLayout content = new VerticalLayout(
                    form,
                    priceGrid);

            content.setPadding(false);
            content.setWidthFull();

            add(content);

            loadProducts();
            loadPrices();
            clearForm();
        }

        private void configureFields() {
            productField.setItemLabelGenerator(SupplierView.this::formatProduct);

            productField.setRequired(true);

            priceField.setRequiredIndicatorVisible(true);
            priceField.setSuffixComponent(new Span("Р/кг"));
            priceField.setClearButtonVisible(true);

            startDateField.setRequired(true);
            endDateField.setRequired(true);
        }

        private void configureGrid() {
            priceGrid.addColumn(price -> formatProduct(price.product()))
                    .setHeader("Продукция")
                    .setAutoWidth(true)
                    .setFlexGrow(1);

            priceGrid.addColumn(price -> formatMoney(price.pricePerKg()) + " кг")
                    .setHeader("Цена")
                    .setAutoWidth(true);

            priceGrid.addColumn(price -> formatDate(price.startDate()))
                    .setHeader("Начало")
                    .setAutoWidth(true);

            priceGrid.addColumn(price -> formatDate(price.endDate()))
                    .setHeader("Конец")
                    .setAutoWidth(true);

            priceGrid.addComponentColumn(price -> {
                        com.vaadin.flow.component.button.Button editButton =
                                new com.vaadin.flow.component.button.Button("Изменить");

                        editButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

                        editButton.addClickListener(event -> editPrice(price));

                        com.vaadin.flow.component.button.Button deleteButton =
                                new com.vaadin.flow.component.button.Button("Удалить");

                        deleteButton.addThemeVariants(
                                ButtonVariant.LUMO_TERTIARY,
                                ButtonVariant.LUMO_ERROR);

                        deleteButton.addClickListener(event -> confirmPriceDeletion(price));

                        return new HorizontalLayout(
                                editButton,
                                deleteButton);
                    })
                    .setHeader("")
                    .setAutoWidth(true)
                    .setFlexGrow(0);

            priceGrid.setWidthFull();
            priceGrid.setHeight("300px");
        }

        private void configureButtons() {
            saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

            saveButton.addClickListener(event -> savePrice());

            com.vaadin.flow.component.button.Button closeButton = new com.vaadin.flow.component.button.Button(
                    "Закрыть",
                    event -> close());

            getFooter().add(
                    closeButton,
                    saveButton);
        }

        private void loadProducts() {
            try {
                productField.setItems(productClient.getAll(organizationSession.getAccessToken())
                        .stream()
                        .sorted(Comparator.comparing(SupplierView.this::formatProduct))
                        .toList());
            } catch(RestClientException exception) {
                showError("Не удалось загрузить продукцию.");
            }
        }

        private void loadPrices() {
            try {
                priceGrid.setItems(supplierPriceClient.getAll(
                        organizationSession.getOrganizationId(),
                        organizationSession.getAccessToken()));
            } catch(RestClientException exception) {
                showError("Не удалось загрузить цены.");
            }
        }

        private void savePrice() {
            ProductResponse product = productField.getValue();
            BigDecimal price = priceField.getValue();
            LocalDate startDate = startDateField.getValue();
            LocalDate endDate = endDateField.getValue();

            if(product == null || price == null || startDate == null || endDate == null) {
                showError("Заполните все поля цены.");
                return;
            }

            if(price.compareTo(BigDecimal.ZERO) <= 0) {
                showError("Цена должна быть больше нуля.");
                return;
            }

            if(endDate.isBefore(startDate)) {
                showError("Конец периода не может быть раньше начала.");
                return;
            }

            SaveSupplierPriceRequest request = new SaveSupplierPriceRequest(
                    product.id(),
                    price,
                    startDate,
                    endDate);

            try {
                if(editedPrice == null) {
                    supplierPriceClient.create(
                            organizationSession.getOrganizationId(),
                            request,
                            organizationSession.getAccessToken());
                } else {
                    supplierPriceClient.update(
                            organizationSession.getOrganizationId(),
                            editedPrice.id(),
                            request,
                            organizationSession.getAccessToken());
                }

                clearForm();
                loadPrices();
                loadActivePrices();
                showSuccess("Цена сохранена.");
            } catch(RestClientException exception) {
                showError("Не удалось сохранить цену. Проверьте пересечение периодов.");
            }
        }

        private void editPrice(SupplierPriceResponse price) {
            editedPrice = price;

            productField.setValue(price.product());
            priceField.setValue(price.pricePerKg());
            startDateField.setValue(price.startDate());
            endDateField.setValue(price.endDate());

            saveButton.setText("Сохранить изменения");
        }

        private void confirmPriceDeletion(SupplierPriceResponse price) {
            ConfirmDialog confirmation = new ConfirmDialog();

            confirmation.setHeader("Удалить цену?");
            confirmation.setText("Цена для продукта " + formatProduct(price.product()) + " будет удалена.");

            confirmation.setCancelable(true);
            confirmation.setCancelText("Отмена");
            confirmation.setConfirmText("Удалить");

            confirmation.addConfirmListener(event -> deletePrice(price));

            confirmation.open();
        }

        private void deletePrice(SupplierPriceResponse price) {
            try {
                supplierPriceClient.delete(
                        organizationSession.getOrganizationId(),
                        price.id(),
                        organizationSession.getAccessToken());

                loadPrices();
                loadActivePrices();
                showSuccess("Цена удалена.");
            } catch(RestClientException exception) {
                showError("Не удалось удалить цену.");
            }
        }

        private void clearForm() {
            editedPrice = null;

            productField.clear();
            priceField.clear();
            startDateField.setValue(LocalDate.now());
            endDateField.setValue(LocalDate.now()
                    .plusMonths(1));

            saveButton.setText("Добавить цену");
        }
    }
}
