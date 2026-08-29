CREATE TABLE organizations (
    id UUID NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE ,
    login VARCHAR(50) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('SUPPLIER', 'RECEIVER'))
);

CREATE UNIQUE INDEX uq_organizations_login_lower
    ON organizations (LOWER(login));

CREATE TABLE products (
    id UUID NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    fruit_type VARCHAR(20) NOT NULL,
    variety VARCHAR(50) NOT NULL UNIQUE,

    CHECK (
        (fruit_type = 'APPLE' AND variety IN ('GRANNY_SMITH', 'FUJI'))
            OR (fruit_type = 'PEAR' AND variety IN ('CONFERENCE', 'ABBE_FETEL'))
        )
);

CREATE TABLE supplier_prices (
    id UUID NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    supplier_id UUID NOT NULL REFERENCES organizations (id) ON DELETE RESTRICT,
    product_id UUID NOT NULL REFERENCES products (id) ON DELETE RESTRICT,
    price_per_kg NUMERIC(12, 2) NOT NULL CHECK ( price_per_kg > 0 ),
    start_price_effect DATE NOT NULL,
    end_price_effect DATE NOT NULL,

    UNIQUE (
           supplier_id,
           product_id,
           start_price_effect,
           end_price_effect
        ),

    CHECK ( start_price_effect <= end_price_effect )
);

CREATE INDEX idx_supplier_prices_product_id ON supplier_prices (product_id);

CREATE TABLE deliveries (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    supplier_id UUID NOT NULL REFERENCES organizations (id) ON DELETE RESTRICT,
    receiver_id UUID NOT NULL REFERENCES organizations (id) ON DELETE RESTRICT,
    delivery_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CHECK ( supplier_id <> receiver_id )
);

CREATE INDEX idx_deliveries_supplier_id_delivery_at ON deliveries(
                                                                 supplier_id,
                                                                 delivery_at
    );

CREATE INDEX idx_deliveries_receiver_id_delivery_at ON deliveries(
                                                                 receiver_id,
                                                                 delivery_at
    );

CREATE TABLE delivery_items (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    delivery_id UUID NOT NULL REFERENCES deliveries (id) ON DELETE CASCADE,
    product_id UUID NOT NULL REFERENCES products (id) ON DELETE RESTRICT,
    weight_kg NUMERIC(12, 3) NOT NULL CHECK ( weight_kg > 0 ),
    price_per_kg NUMERIC(12, 2) NOT NULL CHECK ( price_per_kg > 0 ),

    UNIQUE (delivery_id, product_id)
);

CREATE INDEX idx_delivery_items_product_id ON delivery_items(product_id);

INSERT INTO organizations (
    name,
    login,
    password_hash,
    role
)
VALUES
    (
        'ООО "Сады Придонья"',
        'sady_pridonya',
        '$2a$12$Yus/ocAmDJg7Of1lhVO2xerb21s1DmWlN1ZRwvLD2K1AdctnJ.FNS',
        'SUPPLIER'
    ),
    (
        'ООО "ТК ЭКОФРУКТ"',
        'tk_ecofruit',
        '$2a$12$csmW3hp49TbCwtUjpRCMOuvrrZQX0B0CMR3bwFdMYju/rbq68Ee4m',
        'SUPPLIER'
    ),
    (
        'ООО "АЛ ФРУТ"',
        'al_fruit',
        '$2a$12$eM5sONNfCHlZ5xd/Rbn94OAjt0Waj89WnVPi/tVvkmldz6TFNnSeW',
        'SUPPLIER'
    ),
    (
        'Магнит',
        'magnit',
        '$2a$12$.QBfxZw3jJ9c5lO2pZgHPeCgAzZXYPw1TP.Bjx9ht4gvCzmH9qlsW',
        'RECEIVER'
    ),
    (
        'Пятерочка',
        'pyaterochka',
        '$2a$12$./zy.Iyj/V7xgacRtTyPIOevPzODTNHrofEvWtQ1Rv/GutuRakV..',
        'RECEIVER'
    ),
    (
        'Макси',
        'maksi',
        '$2a$12$tGMOIWoOvfC.HrkeKt7kdeHSpcv.4ikc2s9N65oNAn1O3rdW6Ewpi',
        'RECEIVER'
    );

INSERT INTO products (
    fruit_type,
    variety
)
VALUES
    (
        'APPLE',
        'GRANNY_SMITH'
    ),
    (
        'APPLE',
        'FUJI'
    ),
    (
        'PEAR',
        'CONFERENCE'
    ),
    (
        'PEAR',
        'ABBE_FETEL'
    );