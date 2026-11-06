package com.workintech.ecommerce.service;

import com.workintech.ecommerce.repository.CategoryRepository;
import com.workintech.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogServiceImplTest {

    @Mock private CategoryRepository categoryRepository;
    @Mock private ProductRepository productRepository;
    @InjectMocks private CatalogServiceImpl service;

    @Test
    void defaultSortIsId() {
        assertEquals("id ASC", service.orderBy(null));
        assertEquals("id ASC", service.orderBy(" "));
    }

    @Test
    void parsesSortValues() {
        assertEquals("price DESC, id ASC", service.orderBy("price:desc"));
        assertEquals("price ASC, id ASC", service.orderBy("price:asc"));
        assertEquals("rating DESC, id ASC", service.orderBy("rating:desc"));
        assertEquals("rating ASC, id ASC", service.orderBy("rating:asc"));
        assertEquals("price ASC, id ASC", service.orderBy("price"));
        assertEquals("price ASC, id ASC", service.orderBy("price:up"));
        assertEquals("price DESC, id ASC", service.orderBy("price:DESC"));
        assertEquals("id DESC", service.orderBy("id:desc"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "abc:asc", ":asc", "images:asc"})
    void rejectsUnknownSortColumn(String sort) {
        ResponseStatusException exception =
            assertThrows(ResponseStatusException.class, () -> service.orderBy(sort));
        assertEquals(HttpStatus.BAD_REQUEST.value(), exception.getStatusCode().value());
    }

    @Test
    void parsesLimit() {
        assertEquals(25, service.parseLimit(null));
        assertEquals(25, service.parseLimit(""));
        assertEquals(0, service.parseLimit("0"));
        assertEquals(1, service.parseLimit("1.5"));
        assertNull(service.parseLimit("abc"));
        assertNull(service.parseLimit("-1"));
    }

    @Test
    void parsesOffset() {
        assertEquals(0, service.parseOffset(null));
        assertEquals(0, service.parseOffset("abc"));
        assertEquals(12, service.parseOffset("12.5"));
        assertThrows(ResponseStatusException.class, () -> service.parseOffset("-1"));
    }

    @Test
    void missingProductIsNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException exception =
            assertThrows(ResponseStatusException.class, () -> service.findProduct(1L));

        assertEquals(HttpStatus.NOT_FOUND.value(), exception.getStatusCode().value());
    }
}
