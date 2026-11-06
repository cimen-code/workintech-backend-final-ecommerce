package com.workintech.ecommerce.dto;

import java.math.BigDecimal;
import java.util.List;

public record OrderItemResponse(
    Long id,
    Long productId,
    String name,
    List<ImageResponse> images,
    BigDecimal price,
    Integer count,
    String detail
) {
}
