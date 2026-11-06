#!/usr/bin/env python3
"""Workintech API'sindeki kategori ve urunleri SQL'e cevirir (elle veri girmemek icin).

Kullanim: python3 database/generate_catalog_seed.py
Cikti:    src/main/resources/db/migration/V3__seed_catalog.sql

API'den yalniz okuma yapar. Uretilen SQL repoya eklenir; uygulama acilirken Flyway calistirir,
yani calisma aninda Workintech API'sine bagimli degildir.
"""
import json
import urllib.request
from pathlib import Path

BASE = "https://workintech-fe-ecommerce.onrender.com"
OUT = Path(__file__).resolve().parents[1] / "src/main/resources/db/migration/V3__seed_catalog.sql"


def fetch(path):
    req = urllib.request.Request(BASE + path, headers={"User-Agent": "catalog-seed-generator"})
    with urllib.request.urlopen(req, timeout=120) as res:
        return json.load(res)


def q(value):
    return "'" + str(value).replace("'", "''") + "'"


def main():
    categories = sorted(fetch("/categories"), key=lambda c: c["id"])
    data = fetch("/products?limit=1000")
    products = sorted(data["products"], key=lambda p: p["id"])
    if len(products) != data["total"]:
        raise SystemExit(f"Eksik urun: {len(products)} / {data['total']}")

    lines = [
        "-- database/generate_catalog_seed.py ile uretildi, elle degistirmeyin.",
        f"-- {len(categories)} kategori, {len(products)} urun.",
        "",
        "INSERT INTO categories (id, code, title, img, rating, gender) VALUES",
    ]
    lines.append(",\n".join(
        f"({c['id']}, {q(c['code'])}, {q(c['title'])}, {q(c['img'])}, {c['rating']}, {q(c['gender'])})"
        for c in categories) + ";")

    lines += ["", "INSERT INTO products (id, name, description, price, stock, store_id, category_id, rating, sell_count) VALUES"]
    lines.append(",\n".join(
        f"({p['id']}, {q(p['name'])}, {q(p['description'])}, {p['price']}, {p['stock']}, "
        f"{p['store_id']}, {p['category_id']}, {p['rating']}, {p['sell_count']})"
        for p in products) + ";")

    lines += ["", "INSERT INTO product_images (product_id, url, image_index) VALUES"]
    lines.append(",\n".join(
        f"({p['id']}, {q(i['url'])}, {i['index']})" for p in products for i in p["images"]) + ";")

    lines += [
        "",
        "-- Sabit id'lerle ekleme yapildi; yeni kayitlar cakismasin diye sayaclari ilerlet.",
        "SELECT setval('categories_id_seq', (SELECT MAX(id) FROM categories));",
        "SELECT setval('products_id_seq', (SELECT MAX(id) FROM products));",
        "",
    ]
    OUT.parent.mkdir(parents=True, exist_ok=True)
    OUT.write_text("\n".join(lines), encoding="utf-8")
    print(f"{OUT}: {len(categories)} kategori, {len(products)} urun")


if __name__ == "__main__":
    main()
