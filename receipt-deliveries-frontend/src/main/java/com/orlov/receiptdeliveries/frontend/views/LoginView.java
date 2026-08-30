package com.orlov.receiptdeliveries.frontend.views;

import com.orlov.receiptdeliveries.client.AuthenticationClient;
import com.orlov.receiptdeliveries.contracts.authorization.LoginResponse;
import com.orlov.receiptdeliveries.contracts.authorization.OrganizationRole;
import com.orlov.receiptdeliveries.frontend.session.OrganizationSession;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Route("login")
@RouteAlias("")
@PageTitle("Авторизация | Receipt Deliveries")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final AuthenticationClient authenticationClient;

    private final OrganizationSession organizationSession;

    private final TextField loginField = new TextField("Логин");

    private final PasswordField passwordField = new PasswordField("Пароль");

    private final Button loginButton = new Button("Войти");

    public LoginView(AuthenticationClient authenticationClient,
                     OrganizationSession organizationSession) {
        this.authenticationClient = authenticationClient;
        this.organizationSession = organizationSession;

        configureLayout();
        configureFields();
        configureLoginButton();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if(!organizationSession.isAuthenticated())
            return;

        openAvailableView();
    }

    private void configureLayout() {
        setSizeFull();
        setPadding(true);
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        H1 title = new H1("Receipt Deliveries");

        title.getStyle()
                .set(
                        "margin-bottom",
                        "0");

        VerticalLayout loginPanel = new VerticalLayout(
                title,
                loginField,
                passwordField,
                loginButton);

        loginPanel.setWidthFull();
        loginPanel.setMaxWidth("420px");
        loginPanel.setPadding(true);
        loginPanel.setSpacing(true);
        loginPanel.setAlignItems(Alignment.STRETCH);

        loginPanel.getStyle()
                .set(
                        "border",
                        "1px solid var(--vaadin-border-color)")
                .set(
                        "border-radius",
                        "var(--vaadin-radius-l)")
                .set(
                        "background",
                        "var(--aura-surface-color)")
                .set(
                        "box-shadow",
                        "var(--vaadin-shadow-m)");

        add(loginPanel);
    }

    private void configureFields() {
        loginField.setRequired(true);
        loginField.setAutofocus(true);
        loginField.setClearButtonVisible(true);
        loginField.setPlaceholder("Введите логин организации поставщика или приемщика");

        passwordField.setRequired(true);
        passwordField.setPlaceholder("Введите пароль");
    }

    private void configureLoginButton() {
        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        loginButton.addClickShortcut(Key.ENTER);
        loginButton.addClickListener(event -> login());
    }

    private void login() {
        if(loginField.isEmpty() || passwordField.isEmpty()) {
            showError("Введите логин и пароль");
            return;
        }

        loginButton.setEnabled(false);

        try {
            LoginResponse response = authenticationClient.login(
                    loginField.getValue()
                            .trim(),
                    passwordField.getValue());

            organizationSession.authenticate(response);
            openAvailableView();
        } catch(RestClientResponseException ex) {
            passwordField.clear();
            passwordField.focus();

            showError("Неверный логин или пароль");
        } catch(RestClientException ex) {
            showError("Ошибка сервера");
        } finally {
            loginButton.setEnabled(true);
        }
    }

    private void openAvailableView() {
        if(organizationSession.getRole() == OrganizationRole.SUPPLIER) {
            UI.getCurrent()
                    .navigate(SupplierView.class);
            return;
        }

        UI.getCurrent()
                .navigate(ReceiverView.class);
    }

    private void showError(String message) {
        Notification notification = com.vaadin.flow.component.notification.Notification.show(
                message,
                5000,
                com.vaadin.flow.component.notification.Notification.Position.MIDDLE);

        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}
