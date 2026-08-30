package com.orlov.receiptdeliveries.frontend.views;

import com.orlov.receiptdeliveries.client.DeliveryClient;
import com.orlov.receiptdeliveries.client.OrganizationClient;
import com.orlov.receiptdeliveries.client.ProductClient;
import com.orlov.receiptdeliveries.contracts.authorization.OrganizationRole;
import com.orlov.receiptdeliveries.contracts.delivery.*;
import com.orlov.receiptdeliveries.contracts.deliveryitem.DeliveryItemResponse;
import com.orlov.receiptdeliveries.contracts.organization.OrganizationResponse;
import com.orlov.receiptdeliveries.contracts.product.ProductResponse;
import com.orlov.receiptdeliveries.frontend.layouts.MainLayout;
import com.orlov.receiptdeliveries.frontend.session.OrganizationSession;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
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
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Route(
        value = ReceiverView.ROUTE,
        layout = MainLayout.class
)
@PageTitle("Приёмка | Receipt Deliveries")
public class ReceiverView extends VerticalLayout implements BeforeEnterObserver {

    public static final String ROUTE = "receiving";

    private static final Locale RUSSIAN_LOCALE = Locale.forLanguageTag("ru-RU");

    private static final ZoneId ZONE = ZoneId.of("Europe/Moscow");

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(
            "d MMMM yyyy, HH:mm",
            RUSSIAN_LOCALE);

    private final DeliveryClient deliveryClient;

    private final OrganizationClient organizationClient;

    private final ProductClient productClient;

    private final OrganizationSession organizationSession;

    private final Grid<DeliveryResponse> deliveryGrid = new Grid<>(
            DeliveryResponse.class,
            false);

    private final Span periodLabel = new Span();

    private final Span deliveryCountValue = new Span();

    private final Span totalWeightValue = new Span();

    private final Span totalCostValue = new Span();

    private List<DeliveryResponse> deliveries = List.of();

    public ReceiverView(DeliveryClient deliveryClient,
                        OrganizationClient organizationClient,
                        ProductClient productClient,
                        OrganizationSession organizationSession) {
        this.deliveryClient = deliveryClient;
        this.organizationClient = organizationClient;
        this.productClient = productClient;
        this.organizationSession = organizationSession;

        configurePage();
        configureDeliveryGrid();
        createContent();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if(!organizationSession.isAuthenticated() || organizationSession.getRole() != OrganizationRole.RECEIVER)
            return;

        loadDeliveries();
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
        H1 title = new H1("Приемка");

        title.getStyle()
                .set(
                        "margin-bottom",
                        "0");

        Button reportButton = new Button("Отчет за период");

        reportButton.addClickListener(event -> new DeliveryReportDialog().open());

        Button registerButton = new Button("Зарегистрировать поставку");

        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        registerButton.addClickListener(event -> new DeliveryRegistrationDialog().open());

        HorizontalLayout actions = new HorizontalLayout(
                reportButton,
                registerButton);

        actions.getStyle()
                .set(
                        "flex-wrap",
                        "wrap");

        add(
                title,
                actions,
                createSummary(),
                createDeliverySection());
    }

    private HorizontalLayout createSummary() {
        HorizontalLayout summary = new HorizontalLayout(
                createSummaryItem(
                        periodLabel,
                        deliveryCountValue),
                createSummaryItem(
                        new Span("Общий вес"),
                        totalWeightValue),
                createSummaryItem(
                        new Span("Стоимость"),
                        totalCostValue));

        summary.setWidthFull();
        summary.getStyle()
                .set(
                        "flex-wrap",
                        "wrap");

        return summary;
    }

    private VerticalLayout createSummaryItem(Span label,
                                             Span value) {
        value.getStyle()
                .set(
                        "font-weight",
                        "600");

        VerticalLayout item = new VerticalLayout(
                label,
                value);

        item.setPadding(true);
        item.setSpacing(false);
        item.setMinWidth("220px");

        item.getStyle()
                .set(
                        "border",
                        "1px solid var(--vaadin-border-color)")
                .set(
                        "border-radius",
                        "var(--vaadin-radius-m)")
                .set(
                        "background",
                        "var(--aura-surface-color)");

        return item;
    }

    private VerticalLayout createDeliverySection() {
        H3 title = new H3("Зарегистрированные поставки");

        VerticalLayout section = new VerticalLayout(
                title,
                deliveryGrid);

        section.setWidthFull();
        section.setPadding(false);

        return section;
    }

    private void configureDeliveryGrid() {
        deliveryGrid.addColumn(delivery -> delivery.supplier()
                        .name())
                .setHeader("Поставщик")
                .setAutoWidth(true)
                .setFlexGrow(1);

        deliveryGrid.addColumn(delivery -> formatDateTime(delivery.deliveryAt()))
                .setHeader("Дата и время")
                .setAutoWidth(true);

        deliveryGrid.addColumn(this::formatProducts)
                .setHeader("Продукция")
                .setAutoWidth(true)
                .setFlexGrow(1);

        deliveryGrid.addColumn(delivery -> formatWeight(delivery.totalWeightKg()))
                .setHeader("Вес")
                .setAutoWidth(true);

        deliveryGrid.addColumn(delivery -> formatMoney(delivery.totalCost()))
                .setHeader("Стоимость")
                .setAutoWidth(true);

        deliveryGrid.addComponentColumn(delivery -> {
                    Button openButton = new Button("Открыть");

                    openButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

                    openButton.addClickListener(event -> openDeliveryDetails(delivery));

                    return openButton;
                })
                .setHeader("")
                .setAutoWidth(true)
                .setFlexGrow(0);

        deliveryGrid.setWidthFull();
        deliveryGrid.setHeight("360px");
    }

    private void loadDeliveries() {
        try {
            deliveries = deliveryClient.getAllForReceiver(
                            organizationSession.getOrganizationId(),
                            organizationSession.getAccessToken())
                    .stream()
                    .sorted(Comparator.comparing(DeliveryResponse::deliveryAt)
                            .reversed())
                    .toList();

            updateDeliveries();
        } catch(RestClientException exception) {
            showError("Не удалось загрузить поставки.");
        }
    }

    private void updateDeliveries() {
        deliveryGrid.setItems(deliveries);

        BigDecimal totalWeight = deliveries.stream()
                .map(DeliveryResponse::totalWeightKg)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add);

        BigDecimal totalCost = deliveries.stream()
                .map(DeliveryResponse::totalCost)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add);

        periodLabel.setText("За всё время");
        deliveryCountValue.setText(formatDeliveryCount(deliveries.size()));
        totalWeightValue.setText(formatWeight(totalWeight));
        totalCostValue.setText(formatMoney(totalCost));
    }

    private void openDeliveryDetails(DeliveryResponse delivery) {
        Dialog dialog = new Dialog();

        dialog.setHeaderTitle("Информация о поставке");
        dialog.setWidth("800px");
        dialog.setMaxWidth("95vw");

        VerticalLayout information = new VerticalLayout(
                new Span("Поставщик " + delivery.supplier()
                        .name()),
                new Span("Дата " + formatDateTime(delivery.deliveryAt())),
                new Span("Общий вес " + formatWeight(delivery.totalWeightKg())),
                new Span("Стоимость " + formatMoney(delivery.totalCost())));

        information.setPadding(false);

        Grid<DeliveryItemResponse> itemGrid = new Grid<>(
                DeliveryItemResponse.class,
                false);

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

        Button closeButton = new Button(
                "Закрыть",
                event -> dialog.close());

        dialog.add(
                information,
                itemGrid);
        dialog.getFooter()
                .add(closeButton);
        dialog.open();
    }

    private class DeliveryRegistrationDialog extends Dialog {

        private final ComboBox<OrganizationResponse> supplierField = new ComboBox<>("Поставщик");

        private final DateTimePicker deliveryAtField = new DateTimePicker("Дата и время поставки");

        private final FormLayout productForm = new FormLayout();

        private final Map<ProductResponse, BigDecimalField> weightFields = new LinkedHashMap<>();

        DeliveryRegistrationDialog() {
            setHeaderTitle("Регистрация поставки");
            setWidth("850px");
            setMaxWidth("95vw");

            configureFields();
            configureButtons();
            loadReferenceData();

            VerticalLayout content = new VerticalLayout(
                    supplierField,
                    deliveryAtField,
                    new H3("Поступившая продукция"),
                    productForm);

            content.setPadding(false);
            content.setWidthFull();

            add(content);
        }

        private void configureFields() {
            supplierField.setWidthFull();
            supplierField.setRequired(true);

            supplierField.setItemLabelGenerator(OrganizationResponse::name);

            deliveryAtField.setWidthFull();
            deliveryAtField.setRequiredIndicatorVisible(true);
            deliveryAtField.setStep(Duration.ofMinutes(15));

            deliveryAtField.setValue(LocalDateTime.now()
                    .withSecond(0)
                    .withNano(0));

            productForm.setAutoResponsive(true);
            productForm.setColumnWidth("16rem");
            productForm.setExpandFields(true);
        }

        private void configureButtons() {
            Button saveButton = new Button(
                    "Зарегистрировать",
                    event -> registerDelivery());

            saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

            Button cancelButton = new Button(
                    "Отмена",
                    event -> close());

            getFooter().add(
                    cancelButton,
                    saveButton);
        }

        private void loadReferenceData() {
            try {
                supplierField.setItems(organizationClient.getAllByRole(
                        OrganizationRole.SUPPLIER,
                        organizationSession.getAccessToken()));

                List<ProductResponse> products = productClient.getAll(organizationSession.getAccessToken())
                        .stream()
                        .sorted(Comparator.comparing(ReceiverView.this::formatProduct))
                        .toList();

                createWeightFields(products);
            } catch(RestClientException exception) {
                showError("Не удалось загрузить данные для регистрации поставки.");
            }
        }

        private void createWeightFields(List<ProductResponse> products) {
            productForm.removeAll();
            weightFields.clear();

            for(ProductResponse product : products) {
                BigDecimalField weightField = new BigDecimalField(formatProduct(product));

                weightField.setSuffixComponent(new Span("кг"));

                weightField.setClearButtonVisible(true);

                weightFields.put(
                        product,
                        weightField);

                productForm.add(weightField);
            }
        }

        private void registerDelivery() {
            OrganizationResponse supplier = supplierField.getValue();

            LocalDateTime deliveryAt = deliveryAtField.getValue();

            if(supplier == null || deliveryAt == null) {
                showError("Укажите поставщика, дату и время.");
                return;
            }

            List<CreateDeliveryItemRequest> items = weightFields.entrySet()
                    .stream()
                    .filter(entry -> entry.getValue()
                                             .getValue() != null && entry.getValue()
                                                                            .getValue()
                                                                            .compareTo(BigDecimal.ZERO) > 0)
                    .map(entry -> new CreateDeliveryItemRequest(
                            entry.getKey()
                                    .id(),
                            entry.getValue()
                                    .getValue()))
                    .toList();

            if(items.isEmpty()) {
                showError("Укажите вес хотя бы одного продукта.");
                return;
            }

            OffsetDateTime deliveryOffsetDateTime = deliveryAt.atZone(ZONE)
                    .toOffsetDateTime();

            CreateDeliveryRequest request = new CreateDeliveryRequest(
                    supplier.id(),
                    deliveryOffsetDateTime,
                    items);

            try {
                deliveryClient.register(
                        organizationSession.getOrganizationId(),
                        request,
                        organizationSession.getAccessToken());

                close();
                loadDeliveries();

                showSuccess("Поставка зарегистрирована.");
            } catch(RestClientException exception) {
                showError("Не удалось зарегистрировать поставку. Проверьте действующие цены поставщика.");
            }
        }
    }

    private class DeliveryReportDialog extends Dialog {

        private final DateTimePicker fromField = new DateTimePicker("Начало периода");

        private final DateTimePicker toField = new DateTimePicker("Конец периода");

        private final Span deliveryCount = new Span("Поставок -");
        private final Span totalWeight = new Span("Общий вес -");
        private final Span totalCost = new Span("Стоимость -");

        private final Grid<DeliveryReportItemResponse> reportGrid = new Grid<>(
                DeliveryReportItemResponse.class,
                false);

        DeliveryReportDialog() {
            setHeaderTitle("Отчёт за период");
            setWidth("950px");
            setMaxWidth("95vw");

            configurePeriodFields();
            configureGrid();
            configureButtons();

            HorizontalLayout periodFields = new HorizontalLayout(
                    fromField,
                    toField);

            periodFields.setWidthFull();
            periodFields.getStyle()
                    .set(
                            "flex-wrap",
                            "wrap");

            HorizontalLayout summary = new HorizontalLayout(
                    deliveryCount,
                    totalWeight,
                    totalCost);

            summary.setWidthFull();
            summary.getStyle()
                    .set(
                            "flex-wrap",
                            "wrap");

            VerticalLayout content = new VerticalLayout(
                    periodFields,
                    summary,
                    reportGrid);

            content.setPadding(false);
            content.setWidthFull();

            add(content);
        }

        private void configurePeriodFields() {
            fromField.setStep(Duration.ofMinutes(30));
            toField.setStep(Duration.ofMinutes(30));

            fromField.setValue(LocalDate.now()
                    .atStartOfDay());

            toField.setValue(LocalDate.now()
                    .plusDays(1)
                    .atStartOfDay());

            fromField.addValueChangeListener(event -> {
                if(event.getValue() != null)
                    toField.setMin(event.getValue());
            });
        }

        private void configureGrid() {
            reportGrid.addColumn(DeliveryReportItemResponse::supplierName)
                    .setHeader("Поставщик")
                    .setAutoWidth(true)
                    .setFlexGrow(1);

            reportGrid.addColumn(item -> item.variety()
                            .getDisplayName())
                    .setHeader("Продукция")
                    .setAutoWidth(true)
                    .setFlexGrow(1);

            reportGrid.addColumn(item -> formatWeight(item.totalWeightKg()))
                    .setHeader("Общий вес")
                    .setAutoWidth(true);

            reportGrid.addColumn(item -> formatMoney(item.totalCost()))
                    .setHeader("Стоимость")
                    .setAutoWidth(true);

            reportGrid.setItems(List.of());
            reportGrid.setWidthFull();
            reportGrid.setHeight("350px");
        }

        private void configureButtons() {
            Button createButton = new Button(
                    "Сформировать",
                    event -> createReport());

            createButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

            Button closeButton = new Button(
                    "Закрыть",
                    event -> close());

            getFooter().add(
                    closeButton,
                    createButton);
        }

        private void createReport() {
            LocalDateTime from = fromField.getValue();
            LocalDateTime to = toField.getValue();

            if(from == null || to == null) {
                showError("Укажите начало и конец периода.");
                return;
            }

            if(!from.isBefore(to)) {
                showError("Начало периода должно быть раньше конца.");
                return;
            }

            OffsetDateTime fromOffset = from.atZone(ZONE)
                    .toOffsetDateTime();

            OffsetDateTime toOffset = to.atZone(ZONE)
                    .toOffsetDateTime();

            try {
                DeliveryReportResponse report = deliveryClient.getReport(
                        organizationSession.getOrganizationId(),
                        fromOffset,
                        toOffset,
                        organizationSession.getAccessToken());

                deliveryCount.setText("Поставок " + report.deliveryCount());

                totalWeight.setText("Общий вес " + formatWeight(report.totalWeightKg()));

                totalCost.setText("Стоимость " + formatMoney(report.totalCost()));

                reportGrid.setItems(report.items());
            } catch(RestClientException exception) {
                showError("Не удалось сформировать отчёт.");
            }
        }
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

    private String formatDeliveryCount(int count) {
        if(count % 10 == 1 && count % 100 != 11)
            return count + " поставка";

        if(count % 10 >= 2 && count % 10 <= 4 && (count % 100 < 12 || count % 100 > 14))
            return count + " поставки";

        return count + " поставок";
    }

    private String formatDateTime(OffsetDateTime dateTime) {
        return dateTime == null ? "-" : DATE_TIME_FORMATTER.format(dateTime.atZoneSameInstant(ZONE));
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
                                int maximumFractionDigits) {
        if(value == null)
            return "0";

        NumberFormat formatter = NumberFormat.getNumberInstance(RUSSIAN_LOCALE);

        formatter.setMinimumFractionDigits(0);
        formatter.setMaximumFractionDigits(maximumFractionDigits);

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
}