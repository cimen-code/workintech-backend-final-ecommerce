package com.workintech.ecommerce.service;

import com.workintech.ecommerce.dto.CategoryResponse;
import com.workintech.ecommerce.dto.ProductListResponse;
import com.workintech.ecommerce.dto.ProductResponse;
import com.workintech.ecommerce.entity.Category;
import com.workintech.ecommerce.entity.Product;
import com.workintech.ecommerce.repository.CategoryRepository;
import com.workintech.ecommerce.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class CatalogServiceImpl implements CatalogService {

    private static final int DEFAULT_LIMIT = 25;

    // Sort edilebilen kolonlar. Metin kolonlari bayt sirasina gore siralanir.
    private static final Map<String, String> SORT_COLUMNS = Map.of(
        "id", "id",
        "name", "name COLLATE \"C\"",
        "description", "description COLLATE \"C\"",
        "price", "price",
        "stock", "stock",
        "store_id", "store_id",
        "category_id", "category_id",
        "rating", "rating",
        "sell_count", "sell_count");

    // Arama yalniz ASCII harflerde buyuk kucuk ayirmaz; % ve _ joker sayilir.
    private static final String ASCII_LOWER =
        "translate(%s, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')";

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public CatalogServiceImpl(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> findCategories() {
        return categoryRepository.findAll(Sort.by("id")).stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductListResponse findProducts(String category, String filter, String sort, String limit, String offset) {
        String orderBy = orderBy(sort);
        long firstRow = parseOffset(offset);
        Integer maxRows = parseLimit(limit);

        List<String> conditions = new ArrayList<>();
        Map<String, Object> parameters = new HashMap<>();
        if (category != null && !category.isBlank()) {
            Long categoryId = wholeNumber(category.trim());
            if (categoryId == null) {
                return new ProductListResponse(List.of(), 0);
            }
            conditions.add("category_id = :category");
            parameters.put("category", categoryId);
        }
        if (filter != null && !filter.isEmpty()) {
            String pattern = ASCII_LOWER.formatted(":pattern");
            conditions.add("(" + ASCII_LOWER.formatted("name") + " LIKE " + pattern
                + " OR " + ASCII_LOWER.formatted("description") + " LIKE " + pattern + ")");
            parameters.put("pattern", "%" + filter.replace("\\", "\\\\") + "%");
        }
        String where = conditions.isEmpty() ? "" : " WHERE " + String.join(" AND ", conditions);

        Query count = entityManager.createNativeQuery("SELECT count(*) FROM products" + where);
        parameters.forEach(count::setParameter);
        long total = ((Number) count.getSingleResult()).longValue();

        if (maxRows != null && maxRows == 0) {
            return new ProductListResponse(List.of(), total);
        }
        Query list = entityManager.createNativeQuery(
            "SELECT * FROM products" + where + " ORDER BY " + orderBy, Product.class);
        parameters.forEach(list::setParameter);
        list.setFirstResult(Math.toIntExact(firstRow));
        if (maxRows != null) {
            list.setMaxResults(maxRows);
        }
        @SuppressWarnings("unchecked")
        List<Product> products = list.getResultList();
        return new ProductListResponse(products.stream().map(ProductMapper::toResponse).toList(), total);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse findProduct(Long id) {
        return productRepository.findById(id)
            .map(ProductMapper::toResponse)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    }

    // "kolon" ya da "kolon:yon". Yon desc degilse artan siralanir.
    String orderBy(String sort) {
        if (sort == null || sort.isBlank()) {
            return "id ASC";
        }
        String[] parts = sort.split(":", 2);
        String column = SORT_COLUMNS.get(parts[0]);
        if (column == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid sort value");
        }
        boolean descending = parts.length > 1 && "desc".equals(parts[1].toLowerCase(Locale.ROOT));
        String direction = descending ? "DESC" : "ASC";
        return "id".equals(parts[0]) ? "id " + direction : column + " " + direction + ", id ASC";
    }

    // Verilmezse 25, sayi degilse ya da negatifse sinirsiz, ondalikliysa tam kismi.
    Integer parseLimit(String limit) {
        if (limit == null || limit.isBlank()) {
            return DEFAULT_LIMIT;
        }
        BigDecimal value = number(limit);
        if (value == null || value.signum() < 0) {
            return null;
        }
        BigInteger whole = value.toBigInteger();
        return whole.bitLength() < Integer.SIZE - 1 ? whole.intValue() : null;
    }

    // Verilmezse ya da sayi degilse 0, ondalikliysa tam kismi.
    long parseOffset(String offset) {
        BigDecimal value = offset == null ? null : number(offset);
        if (value == null) {
            return 0;
        }
        if (value.signum() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "offset must not be negative");
        }
        BigInteger whole = value.toBigInteger();
        return whole.bitLength() < Integer.SIZE - 1 ? whole.longValue() : Integer.MAX_VALUE;
    }

    private BigDecimal number(String text) {
        try {
            return new BigDecimal(text.trim());
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private Long wholeNumber(String text) {
        try {
            return Long.valueOf(text);
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(category.getId(), category.getCode(), category.getTitle(), category.getImg(),
            category.getRating(), category.getGender());
    }
}
