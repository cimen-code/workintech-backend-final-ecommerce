package com.workintech.ecommerce.service;

import com.workintech.ecommerce.dto.CardRequest;
import com.workintech.ecommerce.dto.CardResponse;
import com.workintech.ecommerce.dto.CardUpdateRequest;
import com.workintech.ecommerce.entity.Card;
import com.workintech.ecommerce.repository.CardRepository;
import com.workintech.ecommerce.exceptions.RecordNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;

    public CardServiceImpl(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CardResponse> findAll(Long userId) {
        return cardRepository.findByUserIdOrderByIdAsc(userId).stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public CardResponse create(Long userId, CardRequest request) {
        Card card = new Card(userId);
        card.update(request.cardNo(), request.expireMonth(), request.expireYear(), request.nameOnCard());
        return toResponse(cardRepository.save(card));
    }

    @Override
    @Transactional
    public CardResponse update(Long userId, CardUpdateRequest request) {
        Card card = findOwned(userId, request.id());
        card.update(request.cardNo(), request.expireMonth(), request.expireYear(), request.nameOnCard());
        return toResponse(card);
    }

    @Override
    @Transactional
    public void delete(Long userId, String cardId) {
        cardRepository.delete(toId(cardId).flatMap(id -> cardRepository.findByIdAndUserId(id, userId))
            .orElseThrow(() -> new RecordNotFoundException("There is no card record found by id: " + cardId)));
    }

    private Optional<Long> toId(String value) {
        try {
            return Optional.of(Long.valueOf(value));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    private Card findOwned(Long userId, Long cardId) {
        return cardRepository.findByIdAndUserId(cardId, userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Card not found"));
    }

    private CardResponse toResponse(Card card) {
        return new CardResponse(card.getId(), card.getUserId(), card.getCardNo(), card.getExpireMonth(),
            card.getExpireYear(), card.getNameOnCard());
    }
}
