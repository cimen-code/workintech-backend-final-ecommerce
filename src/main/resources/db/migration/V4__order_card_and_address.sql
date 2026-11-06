-- Siparis, Workintech API'si gibi kart numarasini ve silinen adresin numarasini korur. CCV yine saklanmaz.
ALTER TABLE orders ADD COLUMN card_no BIGINT;
UPDATE orders SET card_no = card_last4::BIGINT;
ALTER TABLE orders ALTER COLUMN card_no SET NOT NULL;
ALTER TABLE orders DROP COLUMN card_last4;
ALTER TABLE orders DROP CONSTRAINT orders_address_id_fkey;
