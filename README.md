# Workintech E-Commerce Backend

[workintech-frontend-final-ecommerce](https://github.com/cimen-code/workintech-frontend-final-ecommerce) projesinin backend'i. Frontend'in kullandığı Workintech API'si yerine çalışır.

## Çalıştırma

```bash
createdb workintech_ecommerce
DB_USER=<postgres_kullanıcısı> DB_PASSWORD=<şifre> mvn spring-boot:run
```

API `http://localhost:8080` adresinde açılır. İlk açılışta tablolar kurulur, 14 kategori ve 587 ürün yüklenir.

Frontend'i bu backend ile çalıştırmak için:

```bash
VITE_API_URL=http://localhost:8080 npm run dev
```
