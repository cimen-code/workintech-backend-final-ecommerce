package com.workintech.ecommerce.service;

import com.workintech.ecommerce.dto.CategoryResponse;
import com.workintech.ecommerce.dto.ProductListResponse;
import com.workintech.ecommerce.dto.ProductResponse;
import com.workintech.ecommerce.entity.Category;
import com.workintech.ecommerce.entity.Product;
import com.workintech.ecommerce.repository.CategoryRepository;
import com.workintech.ecommerce.repository.ProductRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class CatalogServiceImpl implements CatalogService {

    private static final int MAX_LIMIT = 1000;

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

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
    public ProductListResponse findProducts(Long category, String filter, String sort, int limit, long offset) {
        if (limit < 1 || limit > MAX_LIMIT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "limit must be between 1 and " + MAX_LIMIT);
        }
        if (offset < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "offset must not be negative");
        }
        OffsetPageRequest page = new OffsetPageRequest(offset, limit, parseSort(sort));
        Page<Product> result = productRepository.findAll(matching(category, filter), page);
        return new ProductListResponse(result.getContent().stream().map(ProductMapper::toResponse).toList(),
            result.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse findProduct(Long id) {
        return productRepository.findById(id)
            .map(ProductMapper::toResponse)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    }

    // sort degerleri T14 kartindaki gibi: price:asc, price:desc, rating:asc, rating:desc
    Sort parseSort(String sort) {
        Sort byId = Sort.by("id");
        if (sort == null || sort.isBlank()) {
            return byId;
        }
        return switch (sort) {
            case "price:asc" -> Sort.by(Sort.Direction.ASC, "price").and(byId);
            case "price:desc" -> Sort.by(Sort.Direction.DESC, "price").and(byId);
            case "rating:asc" -> Sort.by(Sort.Direction.ASC, "rating").and(byId);
            case "rating:desc" -> Sort.by(Sort.Direction.DESC, "rating").and(byId);
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid sort value");
        };
    }

    private Specification<Product> matching(Long category, String filter) {
        return (root, query, cb) -> {
            List<Predicate> conditions = new ArrayList<>();
            if (category != null) {
                conditions.add(cb.equal(root.get("category").get("id"), category));
            }
            if (filter != null && !filter.isBlank()) {
                String pattern = "%" + escapeLike(filter.trim().toLowerCase(Locale.ROOT)) + "%";
                conditions.add(cb.or(
                    cb.like(cb.lower(root.get("name")), pattern, '\\'),
                    cb.like(cb.lower(root.get("description")), pattern, '\\')));
            }
            return cb.and(conditions.toArray(new Predicate[0]));
        };
    }

    private String escapeLike(String text) {
        return text.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(category.getId(), category.getCode(), category.getTitle(), category.getImg(),
            category.getRating(), category.getGender());
    }
}
