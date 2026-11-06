package com.workintech.ecommerce.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
    Long id,
    LocalDateTime orderDate,
    BigDecimal price,
    Long addressId,
    String cardName,
    List<OrderItemResponse> products
) {
}
