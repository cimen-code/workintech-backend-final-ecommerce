package com.workintech.ecommerce.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderRequest(
    @NotNull Long addressId,
    @NotNull LocalDateTime orderDate,
    @NotNull Long cardNo,
    @NotBlank @Size(max = 100) String cardName,
    @NotNull @Min(1) @Max(12) Integer cardExpireMonth,
    @NotNull @Min(2000) @Max(2100) Integer cardExpireYear,
    @NotNull @Min(0) @Max(9999) Integer cardCcv,
    @NotNull @DecimalMin("0") BigDecimal price,
    @NotEmpty @Valid List<OrderItemRequest> products
) {
}
