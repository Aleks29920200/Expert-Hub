package com.example.skillsh.domain.dto.payment;


import com.example.skillsh.domain.dto.user.UserDTO;
import com.example.skillsh.domain.entity.enums.OfferStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OfferResponseDTO {
        private Long id;
        private String title;
        private String description;
        private Double price;
        private OfferStatus status;
        private UserDTO seller;
}

