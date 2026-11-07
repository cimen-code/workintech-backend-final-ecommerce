package com.workintech.ecommerce.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignupRequest(
    @NotBlank @Size(max = 100) @Pattern(regexp = "^\\s*\\S.+\\S\\s*$") String name,
    @NotBlank @Size(max = 150) @Pattern(regexp = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]{2,}$") String email,
    @NotBlank @Size(max = 72)
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[^A-Za-z0-9]).{8,}$") String password,
    @NotNull Long roleId,
    @Valid StoreRequest store
) {
}
