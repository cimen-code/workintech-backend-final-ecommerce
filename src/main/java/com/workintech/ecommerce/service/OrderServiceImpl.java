package com.workintech.ecommerce.service;

import com.workintech.ecommerce.dto.OrderItemRequest;
import com.workintech.ecommerce.dto.OrderItemResponse;
import com.workintech.ecommerce.dto.OrderRequest;
import com.workintech.ecommerce.dto.OrderResponse;
import com.workintech.ecommerce.entity.Order;
import com.workintech.ecommerce.entity.OrderItem;
import com.workintech.ecommerce.entity.Product;
import com.workintech.ecommerce.repository.AddressRepository;
import com.workintech.ecommerce.repository.OrderRepository;
import com.workintech.ecommerce.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;

    public OrderServiceImpl(OrderRepository orderRepository, AddressRepository addressRepository,
                            ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.addressRepository = addressRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public OrderResponse create(Long userId, OrderRequest request) {
        addressRepository.findByIdAndUserId(request.addressId(), userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found"));

        // card_ccv dogrulanir ama hicbir yere yazilmaz.
        Order order = new Order(userId, request.addressId(), request.orderDate(), request.cardNo(),
            request.cardName(), request.cardExpireMonth(), request.cardExpireYear(), request.price());

        for (OrderItemRequest item : request.products()) {
            Product product = productRepository.findById(item.productId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Product not found: " + item.productId()));
            order.addItem(product, item.count(), item.detail());
        }
        return toResponse(orderRepository.save(order));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> findAll(Long userId) {
        return orderRepository.findByUserIdOrderByIdAsc(userId).stream()
            .map(this::toResponse)
            .toList();
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> products = order.getItems().stream().map(this::toResponse).toList();
        return new OrderResponse(order.getId(), order.getUserId(), order.getAddressId(), order.getOrderDate(),
            order.getCardNo(), order.getCardName(), order.getCardExpireMonth(), order.getCardExpireYear(),
            order.getPrice(), products);
    }

    private OrderItemResponse toResponse(OrderItem item) {
        Product product = item.getProduct();
        return new OrderItemResponse(product.getId(), product.getName(), product.getDescription(),
            item.getUnitPrice(), item.getCount(), ProductMapper.images(product));
    }
}
