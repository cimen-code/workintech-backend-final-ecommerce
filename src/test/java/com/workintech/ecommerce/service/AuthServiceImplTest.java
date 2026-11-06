package com.workintech.ecommerce.service;

import com.workintech.ecommerce.dto.AuthResponse;
import com.workintech.ecommerce.dto.LoginRequest;
import com.workintech.ecommerce.dto.SignupRequest;
import com.workintech.ecommerce.dto.StoreRequest;
import com.workintech.ecommerce.entity.Role;
import com.workintech.ecommerce.entity.Store;
import com.workintech.ecommerce.entity.User;
import com.workintech.ecommerce.repository.RoleRepository;
import com.workintech.ecommerce.repository.StoreRepository;
import com.workintech.ecommerce.repository.UserRepository;
import com.workintech.ecommerce.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    private static final Role CUSTOMER = new Role(3L, "Müşteri", "customer");
    private static final Role STORE = new Role(2L, "Mağaza", "store");
    private static final String VALID_IBAN = "TR330006100519786457841326";

    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private StoreRepository storeRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;

    private AuthServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new AuthServiceImpl(userRepository, roleRepository, storeRepository, passwordEncoder, jwtService);
    }

    private SignupRequest signup(Long roleId, StoreRequest store) {
        return new SignupRequest("Test User", " Test@Example.COM ", "Test!1234", roleId, store);
    }

    private static HttpStatus status(ResponseStatusException exception) {
        return HttpStatus.valueOf(exception.getStatusCode().value());
    }

    @Test
    void signupHashesPasswordAndNormalizesEmail() {
        when(roleRepository.findById(3L)).thenReturn(Optional.of(CUSTOMER));
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Test!1234")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(call -> call.getArgument(0));

        service.signup(signup(3L, null));

        ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(saved.capture());
        assertEquals("hashed", saved.getValue().getPassword());
        assertEquals("test@example.com", saved.getValue().getEmail());
        verify(storeRepository, never()).save(any());
    }

    @Test
    void signupRejectsDuplicateEmail() {
        when(roleRepository.findById(3L)).thenReturn(Optional.of(CUSTOMER));
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        ResponseStatusException exception =
            assertThrows(ResponseStatusException.class, () -> service.signup(signup(3L, null)));

        assertEquals(HttpStatus.CONFLICT, status(exception));
        verify(userRepository, never()).save(any());
    }

    @Test
    void signupRejectsUnknownRole() {
        when(roleRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException exception =
            assertThrows(ResponseStatusException.class, () -> service.signup(signup(99L, null)));

        assertEquals(HttpStatus.BAD_REQUEST, status(exception));
    }

    @Test
    void storeRoleRequiresStoreInformation() {
        when(roleRepository.findById(2L)).thenReturn(Optional.of(STORE));

        ResponseStatusException exception =
            assertThrows(ResponseStatusException.class, () -> service.signup(signup(2L, null)));

        assertEquals(HttpStatus.BAD_REQUEST, status(exception));
        verify(userRepository, never()).save(any());
    }

    @Test
    void storeRoleSavesStore() {
        when(roleRepository.findById(2L)).thenReturn(Optional.of(STORE));
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Test!1234")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(call -> call.getArgument(0));

        service.signup(signup(2L, new StoreRequest("Mağazam", "05551234567", "T1234V123456", VALID_IBAN)));

        ArgumentCaptor<Store> saved = ArgumentCaptor.forClass(Store.class);
        verify(storeRepository).save(saved.capture());
        assertEquals("T1234V123456", saved.getValue().getTaxNo());
    }

    @Test
    void loginReturnsToken() {
        User user = new User("Cust Omer", "customer@commerce.com", "hashed", CUSTOMER);
        when(userRepository.findByEmail("customer@commerce.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret", "hashed")).thenReturn(true);
        when(jwtService.generate(any())).thenReturn("token-value");

        AuthResponse response = service.login(new LoginRequest("Customer@commerce.com", "secret"));

        assertEquals("Cust Omer", response.name());
        assertEquals("3", response.roleId());
        assertEquals("token-value", response.token());
    }

    @Test
    void loginRejectsWrongPassword() {
        User user = new User("Cust Omer", "customer@commerce.com", "hashed", CUSTOMER);
        when(userRepository.findByEmail("customer@commerce.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> service.login(new LoginRequest("customer@commerce.com", "wrong")));

        assertEquals(HttpStatus.UNAUTHORIZED, status(exception));
    }

    @Test
    void loginRejectsUnknownEmailWithSameMessage() {
        when(userRepository.findByEmail("nobody@commerce.com")).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> service.login(new LoginRequest("nobody@commerce.com", "x")));

        assertEquals("Invalid email or password", exception.getReason());
    }

    @Test
    void verifyRejectsDeletedUser() {
        when(userRepository.findById(9L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> service.verify(9L));

        assertEquals(HttpStatus.UNAUTHORIZED, status(exception));
    }
}
