package com.workintech.ecommerce.dto;

public record AuthResponse(String name, String email, String roleId, String token) {
}
