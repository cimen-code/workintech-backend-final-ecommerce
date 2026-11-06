package com.workintech.ecommerce.dto;

import java.math.BigDecimal;
import java.util.List;

public record OrderItemResponse(
    Long id,
    String name,
    String description,
    BigDecimal price,
    Integer count,
    List<ImageResponse> images
) {
}
