package com.workintech.ecommerce.controller;

import com.workintech.ecommerce.dto.AuthResponse;
import com.workintech.ecommerce.dto.LoginRequest;
import com.workintech.ecommerce.dto.MessageResponse;
import com.workintech.ecommerce.dto.RoleResponse;
import com.workintech.ecommerce.dto.SignupRequest;
import com.workintech.ecommerce.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/roles")
    public List<RoleResponse> roles() {
        return authService.findRoles();
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse signup(@Valid @RequestBody SignupRequest request) {
        return authService.signup(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/verify")
    public AuthResponse verify(@AuthenticationPrincipal Long userId) {
        return authService.verify(userId);
    }
}
