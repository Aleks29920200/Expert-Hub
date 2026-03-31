package com.example.skillsh.services;

import com.example.skillsh.domain.dto.payment.OfferRequestDTO;
import com.example.skillsh.domain.dto.payment.OfferResponseDTO;
import com.example.skillsh.domain.dto.user.UserDTO;
import com.example.skillsh.domain.entity.ServiceOffer;
import com.example.skillsh.domain.entity.User;
import com.example.skillsh.domain.entity.enums.OfferStatus;
import com.example.skillsh.repository.ServiceOfferRepository;
import com.example.skillsh.repository.UserRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminServiceOfferService {

    private final ServiceOfferRepository serviceOfferRepository;
    private final UserRepo userRepo;

    public AdminServiceOfferService(ServiceOfferRepository serviceOfferRepository, UserRepo userRepo) {
        this.serviceOfferRepository = serviceOfferRepository;
        this.userRepo = userRepo;
    }

    // 1. READ ALL
    public List<OfferResponseDTO> getAllOffers() {
        return serviceOfferRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // 2. CREATE
    @Transactional
    public OfferResponseDTO createOffer(OfferRequestDTO dto) {
        ServiceOffer offer = new ServiceOffer();
        mapRequestToEntity(dto, offer);

        // По подразбиране нова оферта е активна
        if (offer.getStatus() == null) {
            offer.setStatus(OfferStatus.ACTIVE);
        }

        ServiceOffer savedOffer = serviceOfferRepository.save(offer);
        return mapToResponseDTO(savedOffer);
    }

    // 3. UPDATE
    @Transactional
    public OfferResponseDTO updateOffer(Long id, OfferRequestDTO dto) {
        ServiceOffer offer = serviceOfferRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Офертата не е намерена!"));

        mapRequestToEntity(dto, offer);
        ServiceOffer updatedOffer = serviceOfferRepository.save(offer);
        return mapToResponseDTO(updatedOffer);
    }

    // 4. DELETE
    public void deleteOffer(Long id) {
        if (!serviceOfferRepository.existsById(id)) {
            throw new RuntimeException("Офертата не съществува!");
        }
        serviceOfferRepository.deleteById(id);
    }

    // Помощен метод: Мапване от Request DTO към Entity
    private void mapRequestToEntity(OfferRequestDTO dto, ServiceOffer offer) {
        offer.setName(dto.getTitle()); // В DTO е title, в Entity e name

        if (dto.getPrice() > 0) {
            offer.setPrice(BigDecimal.valueOf(dto.getPrice()));
        }

        if (dto.getStatus() != null) {
            offer.setStatus(dto.getStatus());
        }

        // Намираме продавача (Seller)
        if (dto.getSellerId() != null) {
            User seller = userRepo.findById(dto.getSellerId())
                    .orElseThrow(() -> new RuntimeException("Продавачът не е намерен!"));
            offer.setSeller(seller);
        }
    }

    // Помощен метод: Мапване от Entity към Response DTO
    private OfferResponseDTO mapToResponseDTO(ServiceOffer offer) {
        OfferResponseDTO response = new OfferResponseDTO();
        response.setId(offer.getId());
        response.setTitle(offer.getName());
        response.setPrice(offer.getPrice() != null ? offer.getPrice().doubleValue() : 0.0);
        response.setStatus(offer.getStatus());

        if (offer.getSeller() != null) {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(offer.getSeller().getId());
            userDTO.setUsername(offer.getSeller().getUsername());
            response.setSeller(userDTO);
        }
        return response;
    }
}