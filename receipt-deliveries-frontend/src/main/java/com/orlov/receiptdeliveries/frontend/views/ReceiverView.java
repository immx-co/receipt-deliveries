package com.orlov.receiptdeliveries.frontend.views;

import com.orlov.receiptdeliveries.frontend.layouts.MainLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(
        value = ReceiverView.ROUTE,
        layout = MainLayout.class
)
@PageTitle("Приёмка | Receipt Deliveries")
public class ReceiverView extends VerticalLayout {

    public static final String ROUTE = "receiving";

    public ReceiverView() {
        setWidthFull();
        setPadding(true);

        add(
                new H1("Приёмка"),
                new Paragraph("Регистрация фактически поступившей продукции"));
    }
}
