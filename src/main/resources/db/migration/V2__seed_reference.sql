INSERT INTO roles (id, name, code) VALUES
(1, 'Yönetici', 'admin'),
(2, 'Mağaza', 'store'),
(3, 'Müşteri', 'customer');

-- T10 kartindaki herkese acik test kullanicilari. Sifre kartta yazili oldugu icin
-- bu hesaplara gercek veri girilmemelidir.
INSERT INTO users (id, name, email, password, role_id) VALUES
(1, 'Ad Min', 'admin@commerce.com', '$2y$10$V2F8Ko.yBNGgllIpiOkz.uUwXCJTxbe99loYMFpOKDRRK4aOZmmDu', 1),
(2, 'Store Owner', 'store@commerce.com', '$2y$10$V2F8Ko.yBNGgllIpiOkz.uUwXCJTxbe99loYMFpOKDRRK4aOZmmDu', 2),
(3, 'Cust Omer', 'customer@commerce.com', '$2y$10$V2F8Ko.yBNGgllIpiOkz.uUwXCJTxbe99loYMFpOKDRRK4aOZmmDu', 3);

INSERT INTO stores (id, user_id, name, phone, tax_no, bank_account) VALUES
(1, 2, 'Workintech Store', '05000000000', 'T0000V000000', 'TR330006100519786457841326');

SELECT setval('roles_id_seq', (SELECT MAX(id) FROM roles));
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
SELECT setval('stores_id_seq', (SELECT MAX(id) FROM stores));
