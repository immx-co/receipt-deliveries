package com.orlov.receiptdeliveries.frontend.layouts;

import com.orlov.receiptdeliveries.contracts.authorization.OrganizationRole;
import com.orlov.receiptdeliveries.frontend.session.OrganizationSession;
import com.orlov.receiptdeliveries.frontend.views.LoginView;
import com.orlov.receiptdeliveries.frontend.views.ReceiverView;
import com.orlov.receiptdeliveries.frontend.views.SupplierView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;

public class MainLayout extends AppLayout implements BeforeEnterObserver {

    private final OrganizationSession organizationSession;

    private final Button supplierButton = new Button("Поставки");

    private final Button receiverButton = new Button("Приёмка");

    private final Span organizationName = new Span();

    private final Button logoutButton = new Button("Выйти");

    public MainLayout(OrganizationSession organizationSession) {
        this.organizationSession = organizationSession;

        configureNavigation();
        configureLogoutButton();
        addToNavbar(createHeader());
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if(!organizationSession.isAuthenticated()) {
            event.rerouteTo(LoginView.class);
            return;
        }

        OrganizationRole role = organizationSession.getRole();

        supplierButton.setEnabled(role == OrganizationRole.SUPPLIER);
        receiverButton.setEnabled(role == OrganizationRole.RECEIVER);

        organizationName.setText(organizationSession.getOrganizationName());

        String path = event.getLocation()
                .getPath();

        if(path.equals(SupplierView.ROUTE) && role != OrganizationRole.SUPPLIER) {
            event.rerouteTo(ReceiverView.class);
            return;
        }

        if(path.equals(ReceiverView.ROUTE) && role != OrganizationRole.RECEIVER) {
            event.rerouteTo(SupplierView.class);
            return;
        }

        updateActiveButton(path);
    }

    private void configureNavigation() {
        supplierButton.addClickListener(event -> UI.getCurrent()
                .navigate(SupplierView.class));

        receiverButton.addClickListener(event -> UI.getCurrent()
                .navigate(ReceiverView.class));

        supplierButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        receiverButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
    }

    private void configureLogoutButton() {
        logoutButton.addClickListener(event -> {
            organizationSession.logout();
            UI.getCurrent()
                    .navigate(LoginView.class);
        });
    }

    private HorizontalLayout createHeader() {
        H2 applicationTitle = new H2("Receipt Deliveries");

        applicationTitle.getStyle()
                .set(
                        "margin",
                        "0")
                .set(
                        "white-space",
                        "nowrap");

        HorizontalLayout navigation = new HorizontalLayout(
                supplierButton,
                receiverButton);

        navigation.setSpacing(false);
        navigation.setAlignItems(FlexComponent.Alignment.CENTER);

        HorizontalLayout leftSection = new HorizontalLayout(
                applicationTitle,
                navigation);

        leftSection.setAlignItems(HorizontalLayout.Alignment.CENTER);

        HorizontalLayout userSection = new HorizontalLayout(
                organizationName,
                logoutButton);

        userSection.setAlignItems(HorizontalLayout.Alignment.CENTER);

        HorizontalLayout header = new HorizontalLayout(
                leftSection,
                userSection);

        header.setWidthFull();
        header.setPadding(true);
        header.setAlignItems(HorizontalLayout.Alignment.CENTER);
        header.setJustifyContentMode(HorizontalLayout.JustifyContentMode.BETWEEN);

        header.getStyle()
                .set(
                        "box-sizing",
                        "border-box")
                .set(
                        "border-bottom",
                        "1px solid var(--vaadin-border-color)");

        return header;
    }

    private void updateActiveButton(String path) {
        setActive(
                supplierButton,
                path.equals(SupplierView.ROUTE));
        setActive(
                receiverButton,
                path.equals(ReceiverView.ROUTE));
    }

    private void setActive(Button button,
                           boolean active) {
        if(active) {
            button.removeThemeVariants(ButtonVariant.LUMO_TERTIARY);
            button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            return;
        }

        button.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
    }
}
