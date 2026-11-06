package com.workintech.ecommerce.service;

import com.workintech.ecommerce.dto.AddressUpdateRequest;
import com.workintech.ecommerce.dto.CardUpdateRequest;
import com.workintech.ecommerce.entity.Address;
import com.workintech.ecommerce.entity.Card;
import com.workintech.ecommerce.repository.AddressRepository;
import com.workintech.ecommerce.repository.CardRepository;
import com.workintech.ecommerce.exceptions.RecordNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddressAndCardServiceTest {

    @Mock private AddressRepository addressRepository;
    @Mock private CardRepository cardRepository;

    private static final AddressUpdateRequest ADDRESS_UPDATE =
        new AddressUpdateRequest(5L, "ev", "Ali", "Baş", "05550000000", "istanbul", "esenler", "mahalle", "detay");
    private static final CardUpdateRequest CARD_UPDATE =
        new CardUpdateRequest(5L, "4111111111111111", 12, 2030, "Ali Baş");

    @Test
    void cannotUpdateAnotherUsersAddress() {
        when(addressRepository.findByIdAndUserId(5L, 1L)).thenReturn(Optional.empty());
        AddressServiceImpl service = new AddressServiceImpl(addressRepository);

        ResponseStatusException exception =
            assertThrows(ResponseStatusException.class, () -> service.update(1L, ADDRESS_UPDATE));

        assertEquals(HttpStatus.NOT_FOUND.value(), exception.getStatusCode().value());
    }

    @Test
    void cannotDeleteAnotherUsersAddress() {
        when(addressRepository.findByIdAndUserId(5L, 1L)).thenReturn(Optional.empty());
        AddressServiceImpl service = new AddressServiceImpl(addressRepository);

        assertThrows(RecordNotFoundException.class, () -> service.delete(1L, "5"));
        verify(addressRepository, never()).delete(any(Address.class));
    }

    @Test
    void cannotUpdateAnotherUsersCard() {
        when(cardRepository.findByIdAndUserId(5L, 1L)).thenReturn(Optional.empty());
        CardServiceImpl service = new CardServiceImpl(cardRepository);

        assertThrows(ResponseStatusException.class, () -> service.update(1L, CARD_UPDATE));
    }

    @Test
    void cannotDeleteAnotherUsersCard() {
        when(cardRepository.findByIdAndUserId(5L, 1L)).thenReturn(Optional.empty());
        CardServiceImpl service = new CardServiceImpl(cardRepository);

        assertThrows(RecordNotFoundException.class, () -> service.delete(1L, "5"));
        verify(cardRepository, never()).delete(any(Card.class));
    }
}
