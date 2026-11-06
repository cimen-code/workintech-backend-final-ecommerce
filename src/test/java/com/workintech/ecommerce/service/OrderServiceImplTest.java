package com.workintech.ecommerce.service;

import com.workintech.ecommerce.dto.OrderItemRequest;
import com.workintech.ecommerce.dto.OrderRequest;
import com.workintech.ecommerce.entity.Address;
import com.workintech.ecommerce.entity.Category;
import com.workintech.ecommerce.entity.Order;
import com.workintech.ecommerce.entity.Product;
import com.workintech.ecommerce.repository.AddressRepository;
import com.workintech.ecommerce.repository.OrderRepository;
import com.workintech.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock private OrderRepository orderRepository;
    @Mock private AddressRepository addressRepository;
    @Mock private ProductRepository productRepository;
    @InjectMocks private OrderServiceImpl service;

    private OrderRequest request() {
        return new OrderRequest(1L, LocalDateTime.of(2026, 1, 10, 14, 18, 30), 4111111111111111L, "Ali Baş", 12, 2030,
            321, new BigDecimal("175.98"), List.of(new OrderItemRequest(2L, 2, "açık mavi")));
    }

    private Product product() {
        Category category = new Category(1L, "k:tisort", "Tişört", "img", 4.2, "k");
        return new Product(2L, "Siyah", "Açıklama", new BigDecimal("145.99"), 10, 1L, category, 1.0, 5);
    }

    @Test
    void keepsOnlyLastFourDigitsAndProductPrice() {
        when(addressRepository.findByIdAndUserId(1L, 7L)).thenReturn(Optional.of(new Address(7L)));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product()));
        when(orderRepository.save(any(Order.class))).thenAnswer(call -> call.getArgument(0));

        service.create(7L, request());

        ArgumentCaptor<Order> saved = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(saved.capture());
        assertEquals("1111", saved.getValue().getCardLast4());
        assertEquals(new BigDecimal("145.99"), saved.getValue().getItems().get(0).getUnitPrice());
        assertEquals(2, saved.getValue().getItems().get(0).getCount());
    }

    @Test
    void rejectsAnotherUsersAddress() {
        when(addressRepository.findByIdAndUserId(1L, 7L)).thenReturn(Optional.empty());

        ResponseStatusException exception =
            assertThrows(ResponseStatusException.class, () -> service.create(7L, request()));

        assertEquals(HttpStatus.NOT_FOUND.value(), exception.getStatusCode().value());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void rejectsUnknownProduct() {
        when(addressRepository.findByIdAndUserId(1L, 7L)).thenReturn(Optional.of(new Address(7L)));
        when(productRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> service.create(7L, request()));
        verify(orderRepository, never()).save(any());
    }
}
