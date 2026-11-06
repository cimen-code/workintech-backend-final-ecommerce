package com.workintech.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;

// Sifre kurali burada yok: T10'daki herkese acik hesaplarin sifresi 6 karakter.
public record LoginRequest(
    @NotBlank String email,
    @NotBlank String password
) {
}
