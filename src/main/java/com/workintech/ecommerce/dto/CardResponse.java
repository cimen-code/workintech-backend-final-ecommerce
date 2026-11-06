package com.workintech.ecommerce.dto;

public record CardResponse(
    Long id,
    Long userId,
    String cardNo,
    Integer expireMonth,
    Integer expireYear,
    String nameOnCard
) {
}
