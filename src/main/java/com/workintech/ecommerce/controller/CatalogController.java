package com.workintech.ecommerce.controller;

import com.workintech.ecommerce.dto.CategoryResponse;
import com.workintech.ecommerce.dto.ProductListResponse;
import com.workintech.ecommerce.dto.ProductResponse;
import com.workintech.ecommerce.service.CatalogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CatalogController {

    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/categories")
    public List<CategoryResponse> categories() {
        return catalogService.findCategories();
    }

    @GetMapping("/products")
    public ProductListResponse products(@RequestParam(required = false) String category,
                                        @RequestParam(required = false) String filter,
                                        @RequestParam(required = false) String sort,
                                        @RequestParam(required = false) String limit,
                                        @RequestParam(required = false) String offset) {
        return catalogService.findProducts(category, filter, sort, limit, offset);
    }

    @GetMapping("/products/{productId}")
    public ProductResponse product(@PathVariable Long productId) {
        return catalogService.findProduct(productId);
    }
}
