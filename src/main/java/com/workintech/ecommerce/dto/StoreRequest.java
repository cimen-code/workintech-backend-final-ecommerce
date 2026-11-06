package com.workintech.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StoreRequest(
    @NotBlank @Size(min = 3, max = 100) String name,
    @NotBlank @Size(max = 20) String phone,
    @NotBlank @Size(max = 12) String taxNo,
    @NotBlank @Size(max = 34) String bankAccount
) {
}
