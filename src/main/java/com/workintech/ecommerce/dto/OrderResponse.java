package com.workintech.ecommerce.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
    Long id,
    Long userId,
    Long addressId,
    LocalDateTime orderDate,
    String cardNo,
    String cardName,
    Integer cardExpireMonth,
    Integer cardExpireYear,
    BigDecimal price,
    List<OrderItemResponse> products
) {
}
