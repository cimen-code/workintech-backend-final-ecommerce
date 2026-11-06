package com.workintech.ecommerce.service;

import com.workintech.ecommerce.dto.AuthResponse;
import com.workintech.ecommerce.dto.LoginRequest;
import com.workintech.ecommerce.dto.MessageResponse;
import com.workintech.ecommerce.dto.RoleResponse;
import com.workintech.ecommerce.dto.SignupRequest;

import java.util.List;

public interface AuthService {

    List<RoleResponse> findRoles();

    MessageResponse signup(SignupRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse verify(Long userId);
}
