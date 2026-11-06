CREATE TABLE roles (
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    code VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE users (
    id       BIGSERIAL PRIMARY KEY,
    name     VARCHAR(100) NOT NULL,
    email    VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role_id  BIGINT NOT NULL REFERENCES roles (id)
);

CREATE TABLE stores (
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT NOT NULL UNIQUE REFERENCES users (id) ON DELETE CASCADE,
    name         VARCHAR(100) NOT NULL,
    phone        VARCHAR(20) NOT NULL,
    tax_no       VARCHAR(12) NOT NULL,
    bank_account VARCHAR(34) NOT NULL
);

CREATE TABLE categories (
    id     BIGSERIAL PRIMARY KEY,
    code   VARCHAR(50) NOT NULL UNIQUE,
    title  VARCHAR(100) NOT NULL,
    img    VARCHAR(500) NOT NULL,
    rating DOUBLE PRECISION NOT NULL,
    gender VARCHAR(1) NOT NULL CHECK (gender IN ('k', 'e'))
);

CREATE TABLE products (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(150) NOT NULL,
    description VARCHAR(500) NOT NULL,
    price       NUMERIC(10, 2) NOT NULL CHECK (price >= 0),
    stock       INTEGER NOT NULL CHECK (stock >= 0),
    store_id    BIGINT NOT NULL REFERENCES stores (id),
    category_id BIGINT NOT NULL REFERENCES categories (id),
    rating      DOUBLE PRECISION NOT NULL,
    sell_count  INTEGER NOT NULL CHECK (sell_count >= 0)
);

CREATE INDEX idx_products_category ON products (category_id);

CREATE TABLE product_images (
    id          BIGSERIAL PRIMARY KEY,
    product_id  BIGINT NOT NULL REFERENCES products (id) ON DELETE CASCADE,
    url         VARCHAR(500) NOT NULL,
    image_index INTEGER NOT NULL,
    UNIQUE (product_id, image_index)
);

CREATE TABLE addresses (
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    title        VARCHAR(100) NOT NULL,
    name         VARCHAR(100) NOT NULL,
    surname      VARCHAR(100) NOT NULL,
    phone        VARCHAR(20) NOT NULL,
    city         VARCHAR(100) NOT NULL,
    district     VARCHAR(100) NOT NULL,
    neighborhood VARCHAR(255) NOT NULL,
    address      VARCHAR(500)
);

CREATE INDEX idx_addresses_user ON addresses (user_id);

-- Siparis ekrani numarayi listeden aldigi icin kart numarasi tam saklanir.
-- Bu yuzden sisteme yalniz test kart numarasi girilmelidir.
CREATE TABLE cards (
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    card_no      VARCHAR(16) NOT NULL,
    expire_month INTEGER NOT NULL CHECK (expire_month BETWEEN 1 AND 12),
    expire_year  INTEGER NOT NULL,
    name_on_card VARCHAR(100) NOT NULL
);

CREATE INDEX idx_cards_user ON cards (user_id);

-- Sipariste kart numarasinin yalniz son 4 hanesi tutulur; CCV hic saklanmaz.
-- Adres silinse de siparis kaydi kalir (address_id bosalir).
CREATE TABLE orders (
    id                BIGSERIAL PRIMARY KEY,
    user_id           BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    address_id        BIGINT REFERENCES addresses (id) ON DELETE SET NULL,
    order_date        TIMESTAMP NOT NULL,
    card_last4        VARCHAR(4) NOT NULL,
    card_name         VARCHAR(100) NOT NULL,
    card_expire_month INTEGER NOT NULL,
    card_expire_year  INTEGER NOT NULL,
    price             NUMERIC(12, 2) NOT NULL CHECK (price >= 0)
);

CREATE INDEX idx_orders_user ON orders (user_id);

CREATE TABLE order_items (
    id         BIGSERIAL PRIMARY KEY,
    order_id   BIGINT NOT NULL REFERENCES orders (id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES products (id),
    count      INTEGER NOT NULL CHECK (count > 0),
    detail     VARCHAR(255),
    unit_price NUMERIC(10, 2) NOT NULL
);

CREATE INDEX idx_order_items_order ON order_items (order_id);
