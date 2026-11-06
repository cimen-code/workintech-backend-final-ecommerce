package com.workintech.ecommerce.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// Frontend id alanini string gonderir ("id": "1"); Jackson bunu Long'a cevirir.
public record CardUpdateRequest(
    @NotNull Long id,
    @NotBlank @Pattern(regexp = "^\\d{16}$") String cardNo,
    @NotNull @Min(1) @Max(12) Integer expireMonth,
    @NotNull @Min(2000) @Max(2100) Integer expireYear,
    @NotBlank @Size(max = 100) String nameOnCard
) {
}
