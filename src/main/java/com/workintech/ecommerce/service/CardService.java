package com.workintech.ecommerce.service;

import com.workintech.ecommerce.dto.CardRequest;
import com.workintech.ecommerce.dto.CardResponse;
import com.workintech.ecommerce.dto.CardUpdateRequest;

import java.util.List;

public interface CardService {

    List<CardResponse> findAll(Long userId);

    CardResponse create(Long userId, CardRequest request);

    CardResponse update(Long userId, CardUpdateRequest request);

    void delete(Long userId, Long cardId);
}
