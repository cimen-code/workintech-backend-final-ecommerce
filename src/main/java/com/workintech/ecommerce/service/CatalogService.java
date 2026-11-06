package com.workintech.ecommerce.service;

import com.workintech.ecommerce.dto.CategoryResponse;
import com.workintech.ecommerce.dto.ProductListResponse;
import com.workintech.ecommerce.dto.ProductResponse;

import java.util.List;

public interface CatalogService {

    List<CategoryResponse> findCategories();

    ProductListResponse findProducts(Long category, String filter, String sort, int limit, long offset);

    ProductResponse findProduct(Long id);
}
