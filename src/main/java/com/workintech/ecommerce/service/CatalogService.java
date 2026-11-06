package com.workintech.ecommerce.service;

import com.workintech.ecommerce.dto.CategoryResponse;
import com.workintech.ecommerce.dto.ProductListResponse;
import com.workintech.ecommerce.dto.ProductResponse;

import java.util.List;

public interface CatalogService {

    List<CategoryResponse> findCategories();

    ProductListResponse findProducts(String category, String filter, String sort, String limit, String offset);

    ProductResponse findProduct(Long id);
}
