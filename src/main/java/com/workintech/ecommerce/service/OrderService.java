package com.workintech.ecommerce.service;

import com.workintech.ecommerce.dto.OrderRequest;
import com.workintech.ecommerce.dto.OrderResponse;

import java.util.List;

public interface OrderService {

    OrderResponse create(Long userId, OrderRequest request);

    List<OrderResponse> findAll(Long userId);
}
