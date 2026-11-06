package com.workintech.ecommerce.controller;

import com.workintech.ecommerce.dto.AddressRequest;
import com.workintech.ecommerce.dto.AddressResponse;
import com.workintech.ecommerce.dto.AddressUpdateRequest;
import com.workintech.ecommerce.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user/address")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping
    public List<AddressResponse> findAll(@AuthenticationPrincipal Long userId) {
        return addressService.findAll(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AddressResponse create(@AuthenticationPrincipal Long userId, @Valid @RequestBody AddressRequest request) {
        return addressService.create(userId, request);
    }

    @PutMapping
    public AddressResponse update(@AuthenticationPrincipal Long userId,
                                  @Valid @RequestBody AddressUpdateRequest request) {
        return addressService.update(userId, request);
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<String> delete(@AuthenticationPrincipal Long userId, @PathVariable String addressId) {
        addressService.delete(userId, addressId);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body("\"Address record deleted!\"");
    }
}
