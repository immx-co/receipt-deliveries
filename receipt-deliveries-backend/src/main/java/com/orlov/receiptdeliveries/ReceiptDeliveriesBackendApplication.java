package com.orlov.receiptdeliveries;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Главный класс приложения.
 */
@SpringBootApplication
public class ReceiptDeliveriesBackendApplication {

    /**
     * Точка входа в приложение.
     * @param args аргументы командной строки.
     */
    static void main(String[] args) {
        SpringApplication.run(
                ReceiptDeliveriesBackendApplication.class,
                args);
    }
}
