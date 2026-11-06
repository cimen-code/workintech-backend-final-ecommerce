package com.workintech.ecommerce.controller;

import com.workintech.ecommerce.dto.CardRequest;
import com.workintech.ecommerce.dto.CardResponse;
import com.workintech.ecommerce.dto.CardUpdateRequest;
import com.workintech.ecommerce.service.CardService;
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
@RequestMapping("/user/card")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping
    public List<CardResponse> findAll(@AuthenticationPrincipal Long userId) {
        return cardService.findAll(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CardResponse create(@AuthenticationPrincipal Long userId, @Valid @RequestBody CardRequest request) {
        return cardService.create(userId, request);
    }

    @PutMapping
    public CardResponse update(@AuthenticationPrincipal Long userId, @Valid @RequestBody CardUpdateRequest request) {
        return cardService.update(userId, request);
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<String> delete(@AuthenticationPrincipal Long userId, @PathVariable String cardId) {
        cardService.delete(userId, cardId);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body("\"Credit card record deleted!\"");
    }
}
