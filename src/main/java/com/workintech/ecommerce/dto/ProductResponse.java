package com.workintech.ecommerce.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProductResponse(
    Long id,
    String name,
    String description,
    BigDecimal price,
    Integer stock,
    Long storeId,
    Long categoryId,
    Double rating,
    Integer sellCount,
    List<ImageResponse> images
) {
}
