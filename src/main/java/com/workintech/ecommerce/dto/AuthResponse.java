package com.workintech.ecommerce.dto;

public record AuthResponse(String name, String email, Long roleId, String token) {
}
