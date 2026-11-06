package com.workintech.ecommerce.dto;

public record AddressResponse(
    Long id,
    Long userId,
    String title,
    String name,
    String surname,
    String phone,
    String city,
    String district,
    String neighborhood,
    String address
) {
}
