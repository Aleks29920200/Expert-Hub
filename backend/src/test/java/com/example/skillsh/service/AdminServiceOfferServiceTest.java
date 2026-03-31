package com.example.skillsh.service;

import com.example.skillsh.domain.dto.payment.OfferRequestDTO;
import com.example.skillsh.domain.dto.payment.OfferResponseDTO;
import com.example.skillsh.domain.entity.ServiceOffer;
import com.example.skillsh.domain.entity.User;
import com.example.skillsh.repository.ServiceOfferRepository;
import com.example.skillsh.repository.UserRepo;
import com.example.skillsh.services.AdminServiceOfferService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceOfferServiceTest {

    @Mock
    private ServiceOfferRepository serviceOfferRepository;

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private AdminServiceOfferService adminServiceOfferService;

    @Test
    void testGetAllOffers() {
        // Arrange
        User seller = new User();
        seller.setId(1L);
        seller.setUsername("test_seller");

        ServiceOffer offer = new ServiceOffer();
        offer.setId(10L);
        offer.setName("Урок по китара");
        offer.setPrice(BigDecimal.valueOf(50.0));
        // Ако имаш enum OfferStatus, например ACTIVE, го сетни:
        // offer.setStatus(OfferStatus.ACTIVE);
        offer.setSeller(seller);

        when(serviceOfferRepository.findAll()).thenReturn(List.of(offer));

        // Act
        List<OfferResponseDTO> result = adminServiceOfferService.getAllOffers();

        // Assert
        assertEquals(1, result.size());
        OfferResponseDTO dto = result.get(0);
        assertEquals(10L, dto.getId());
        assertEquals("Урок по китара", dto.getTitle());
        assertEquals(50.0, dto.getPrice());
        assertNotNull(dto.getSeller());
        assertEquals(1L, dto.getSeller().getId());
        assertEquals("test_seller", dto.getSeller().getUsername());

        verify(serviceOfferRepository, times(1)).findAll();
    }

    @Test
    void testCreateOffer_Success() {
        // Arrange (Подготовка)
        OfferRequestDTO requestDto = new OfferRequestDTO();
        requestDto.setTitle("Почистване на дом");
        requestDto.setPrice(100.0);
        requestDto.setSellerId(5L);

        User seller = new User();
        seller.setId(5L);
        seller.setUsername("cleaner_user");

        when(userRepo.findById(5L)).thenReturn(Optional.of(seller));

        // ТУК Е ФИКСЪТ: Създаваме фалшив обект, който "базата" ще върне
        ServiceOffer mockedSavedOffer = new ServiceOffer();
        mockedSavedOffer.setId(1L); // Задаваме ID, за да не гърми mapToResponseDTO
        mockedSavedOffer.setName("Почистване на дом");
        mockedSavedOffer.setPrice(BigDecimal.valueOf(100.0));
        mockedSavedOffer.setSeller(seller);

        // Казваме на Mockito: Когато някой извика save() с какъвто и да е ServiceOffer, върни mockedSavedOffer
        when(serviceOfferRepository.save(any(ServiceOffer.class))).thenReturn(mockedSavedOffer);

        // Act (Изпълнение)
        OfferResponseDTO responseDTO = adminServiceOfferService.createOffer(requestDto);

        // Assert (Проверка)
        // Проверяваме дали методът връща правилното DTO
        assertNotNull(responseDTO);
        assertEquals(1L, responseDTO.getId());
        assertEquals("Почистване на дом", responseDTO.getTitle());

        // Улавяме какво реално е подадено към метода save()
        ArgumentCaptor<ServiceOffer> offerCaptor = ArgumentCaptor.forClass(ServiceOffer.class);
        verify(serviceOfferRepository, times(1)).save(offerCaptor.capture());

        ServiceOffer capturedOffer = offerCaptor.getValue();
        assertEquals("Почистване на дом", capturedOffer.getName());
        assertEquals(BigDecimal.valueOf(100.0), capturedOffer.getPrice());
        assertEquals(seller, capturedOffer.getSeller());
    }

    @Test
    void testCreateOffer_ThrowsExceptionWhenSellerNotFound() {
        // Arrange
        OfferRequestDTO requestDto = new OfferRequestDTO();
        requestDto.setSellerId(99L); // Несъществуващ продавач

        when(userRepo.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            adminServiceOfferService.createOffer(requestDto);
        });

        assertEquals("Продавачът не е намерен!", exception.getMessage());

        // Уверяваме се, че методът save() не е извикан, защото хвърляме грешка преди това
        verify(serviceOfferRepository, never()).save(any(ServiceOffer.class));
    }

    @Test
    void testDeleteOffer_Success() {
        // Arrange
        Long offerId = 1L;
        when(serviceOfferRepository.existsById(offerId)).thenReturn(true);

        // Act
        adminServiceOfferService.deleteOffer(offerId);

        // Assert
        verify(serviceOfferRepository, times(1)).deleteById(offerId);
    }

    @Test
    void testDeleteOffer_ThrowsExceptionWhenOfferNotFound() {
        // Arrange
        Long offerId = 99L;
        when(serviceOfferRepository.existsById(offerId)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            adminServiceOfferService.deleteOffer(offerId);
        });

        assertEquals("Офертата не съществува!", exception.getMessage());
        verify(serviceOfferRepository, never()).deleteById(anyLong());
    }
}
