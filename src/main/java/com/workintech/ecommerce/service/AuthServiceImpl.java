package com.workintech.ecommerce.service;

import com.workintech.ecommerce.dto.AuthResponse;
import com.workintech.ecommerce.dto.LoginRequest;
import com.workintech.ecommerce.dto.MessageResponse;
import com.workintech.ecommerce.dto.RoleResponse;
import com.workintech.ecommerce.dto.SignupRequest;
import com.workintech.ecommerce.dto.StoreRequest;
import com.workintech.ecommerce.entity.Role;
import com.workintech.ecommerce.entity.Store;
import com.workintech.ecommerce.entity.User;
import com.workintech.ecommerce.repository.RoleRepository;
import com.workintech.ecommerce.repository.StoreRepository;
import com.workintech.ecommerce.repository.UserRepository;
import com.workintech.ecommerce.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final StoreRepository storeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
                           StoreRepository storeRepository, PasswordEncoder passwordEncoder,
                           JwtService jwtService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.storeRepository = storeRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> findRoles() {
        return roleRepository.findAll().stream()
            .map(role -> new RoleResponse(role.getId(), role.getName(), role.getCode()))
            .toList();
    }

    @Override
    @Transactional
    public MessageResponse signup(SignupRequest request) {
        Role role = roleRepository.findById(request.roleId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid role"));

        StoreRequest store = request.store();
        boolean isStore = Role.STORE.equals(role.getCode());
        if (isStore) {
            validateStore(store);
        }

        String email = normalizeEmail(request.email());
        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This email is already registered");
        }

        User user = userRepository.save(
            new User(request.name().trim(), email, passwordEncoder.encode(request.password()), role));

        if (isStore) {
            storeRepository.save(new Store(user, store.name().trim(), store.phone(), store.taxNo(),
                store.bankAccount().toUpperCase(Locale.ROOT)));
        }
        return new MessageResponse("User created");
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(normalizeEmail(request.email()))
            .filter(found -> passwordEncoder.matches(request.password(), found.getPassword()))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
        return toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse verify(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not verified"));
        return toResponse(user);
    }

    private void validateStore(StoreRequest store) {
        if (store == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Store information is required");
        }
    }

    private AuthResponse toResponse(User user) {
        return new AuthResponse(user.getName(), user.getEmail(), String.valueOf(user.getRole().getId()),
            jwtService.generate(user.getId()));
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
