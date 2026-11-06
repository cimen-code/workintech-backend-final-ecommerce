package com.workintech.ecommerce.service;

import com.workintech.ecommerce.dto.AddressRequest;
import com.workintech.ecommerce.dto.AddressResponse;
import com.workintech.ecommerce.dto.AddressUpdateRequest;
import com.workintech.ecommerce.entity.Address;
import com.workintech.ecommerce.repository.AddressRepository;
import com.workintech.ecommerce.exceptions.RecordNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;

    public AddressServiceImpl(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponse> findAll(Long userId) {
        return addressRepository.findByUserIdOrderByIdAsc(userId).stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public AddressResponse create(Long userId, AddressRequest request) {
        Address address = new Address(userId);
        address.update(request.title(), request.name(), request.surname(), request.phone(), request.city(),
            request.district(), request.neighborhood(), request.address());
        return toResponse(addressRepository.save(address));
    }

    @Override
    @Transactional
    public AddressResponse update(Long userId, AddressUpdateRequest request) {
        Address address = findOwned(userId, request.id());
        address.update(request.title(), request.name(), request.surname(), request.phone(), request.city(),
            request.district(), request.neighborhood(), request.address());
        return toResponse(address);
    }

    @Override
    @Transactional
    public void delete(Long userId, String addressId) {
        addressRepository.delete(toId(addressId).flatMap(id -> addressRepository.findByIdAndUserId(id, userId))
            .orElseThrow(() -> new RecordNotFoundException("There is no address record found by id " + addressId)));
    }

    private Optional<Long> toId(String value) {
        try {
            return Optional.of(Long.valueOf(value));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    // Baskasinin kaydi "bulunamadi" doner; boylece var olup olmadigi da disari sizmaz.
    private Address findOwned(Long userId, Long addressId) {
        return addressRepository.findByIdAndUserId(addressId, userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found"));
    }

    private AddressResponse toResponse(Address address) {
        return new AddressResponse(address.getId(), address.getUserId(), address.getTitle(), address.getName(),
            address.getSurname(), address.getPhone(), address.getCity(), address.getDistrict(),
            address.getNeighborhood(), address.getAddress());
    }
}
