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
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogServiceImplTest {

    @Mock private CategoryRepository categoryRepository;
    @Mock private ProductRepository productRepository;
    @InjectMocks private CatalogServiceImpl service;

    @Test
    void defaultSortIsId() {
        assertEquals(Sort.by("id"), service.parseSort(null));
        assertEquals(Sort.by("id"), service.parseSort(" "));
    }

    @Test
    void parsesSortValuesFromCard() {
        assertEquals(Sort.Direction.DESC, service.parseSort("price:desc").getOrderFor("price").getDirection());
        assertEquals(Sort.Direction.ASC, service.parseSort("price:asc").getOrderFor("price").getDirection());
        assertEquals(Sort.Direction.DESC, service.parseSort("rating:desc").getOrderFor("rating").getDirection());
        assertEquals(Sort.Direction.ASC, service.parseSort("rating:asc").getOrderFor("rating").getDirection());
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "price", "name:asc", "price:up"})
    void rejectsUnknownSort(String sort) {
        ResponseStatusException exception =
            assertThrows(ResponseStatusException.class, () -> service.parseSort(sort));
        assertEquals(HttpStatus.BAD_REQUEST.value(), exception.getStatusCode().value());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, 1001})
    void rejectsLimitOutOfRange(int limit) {
        assertThrows(ResponseStatusException.class, () -> service.findProducts(null, null, null, limit, 0));
    }

    @Test
    void rejectsNegativeOffset() {
        assertThrows(ResponseStatusException.class, () -> service.findProducts(null, null, null, 25, -1));
    }

    @Test
    void missingProductIsNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException exception =
            assertThrows(ResponseStatusException.class, () -> service.findProduct(1L));

        assertEquals(HttpStatus.NOT_FOUND.value(), exception.getStatusCode().value());
    }
}
