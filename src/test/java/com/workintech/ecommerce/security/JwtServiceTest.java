package com.workintech.ecommerce.security;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    private static final String SECRET = "test-only-secret-test-only-secret-1234";

    @Test
    void tokenCarriesUserId() {
        JwtService service = new JwtService(SECRET, 7);
        assertEquals(Optional.of(42L), service.parseUserId(service.generate(42L)));
    }

    @Test
    void rejectsGarbageToken() {
        assertTrue(new JwtService(SECRET, 7).parseUserId("not-a-token").isEmpty());
    }

    @Test
    void rejectsTokenSignedWithAnotherKey() {
        String token = new JwtService("another-secret-another-secret-123456", 7).generate(1L);
        assertFalse(new JwtService(SECRET, 7).parseUserId(token).isPresent());
    }

    @Test
    void rejectsExpiredToken() {
        JwtService expired = new JwtService(SECRET, -1);
        assertTrue(expired.parseUserId(expired.generate(1L)).isEmpty());
    }

    @Test
    void shortSecretIsRefused() {
        assertThrows(IllegalStateException.class, () -> new JwtService("short", 7));
    }

    @Test
    void blankSecretGeneratesRandomKey() {
        JwtService service = new JwtService("", 7);
        assertEquals(Optional.of(5L), service.parseUserId(service.generate(5L)));
    }
}
