package com.workintech.ecommerce.service;

import com.workintech.ecommerce.dto.ImageResponse;
import com.workintech.ecommerce.dto.ProductResponse;
import com.workintech.ecommerce.entity.Product;

import java.util.List;

final class ProductMapper {

    private ProductMapper() {
    }

    static ProductResponse toResponse(Product product) {
        return new ProductResponse(product.getId(), product.getName(), product.getDescription(), product.getPrice(),
            product.getStock(), product.getStoreId(), product.getCategory().getId(), product.getRating(),
            product.getSellCount(), images(product));
    }

    static List<ImageResponse> images(Product product) {
        return product.getImages().stream()
            .map(image -> new ImageResponse(image.getUrl(), image.getImageIndex()))
            .toList();
    }
}
