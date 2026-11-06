package com.workintech.ecommerce.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record OrderItemRequest(
    @NotNull Long productId,
    @NotNull @Min(1) Integer count,
    @Size(max = 255) String detail
) {
}
