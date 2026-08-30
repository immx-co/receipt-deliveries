package com.orlov.receiptdeliveries.frontend;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.ColorScheme;
import com.vaadin.flow.theme.aura.Aura;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Точка входа в Frontend приложение.
 */
@ColorScheme(ColorScheme.Value.DARK)
@StyleSheet(Aura.STYLESHEET)
@SpringBootApplication
public class ReceiptDeliveriesFrontendApplication implements AppShellConfigurator {

    static void main(String[] args) {
        SpringApplication.run(
                ReceiptDeliveriesFrontendApplication.class,
                args);
    }
}
