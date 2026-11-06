package com.workintech.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddressRequest(
    @NotBlank @Size(max = 100) String title,
    @NotBlank @Size(max = 100) String name,
    @NotBlank @Size(max = 100) String surname,
    @NotBlank @Size(max = 20) String phone,
    @NotBlank @Size(max = 100) String city,
    @NotBlank @Size(max = 100) String district,
    @NotBlank @Size(max = 255) String neighborhood,
    @Size(max = 500) String address
) {
}
