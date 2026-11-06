package com.workintech.ecommerce.service;

import com.workintech.ecommerce.dto.AddressRequest;
import com.workintech.ecommerce.dto.AddressResponse;
import com.workintech.ecommerce.dto.AddressUpdateRequest;

import java.util.List;

public interface AddressService {

    List<AddressResponse> findAll(Long userId);

    AddressResponse create(Long userId, AddressRequest request);

    AddressResponse update(Long userId, AddressUpdateRequest request);

    void delete(Long userId, String addressId);
}
