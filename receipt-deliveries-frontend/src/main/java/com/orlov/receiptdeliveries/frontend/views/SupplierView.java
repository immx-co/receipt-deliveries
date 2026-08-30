package com.orlov.receiptdeliveries.frontend.views;

import com.orlov.receiptdeliveries.frontend.layouts.MainLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(
        value = SupplierView.ROUTE,
        layout = MainLayout.class
)
@PageTitle("Поставки | Receipt Deliveries")
public class SupplierView extends VerticalLayout {

    public static final String ROUTE = "supplies";

    public SupplierView() {
        setWidthFull();
        setPadding(true);

        add(
                new H1("Поставки"),
                new Paragraph("История зарегистрированных поставок и действующие цены."));
    }
}
