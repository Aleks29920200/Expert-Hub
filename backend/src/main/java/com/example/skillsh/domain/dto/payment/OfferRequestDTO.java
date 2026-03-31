package com.example.skillsh.domain.dto.payment;


import com.example.skillsh.domain.entity.enums.OfferStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OfferRequestDTO {
    private String title;
    private String description;
    private double price;
    private double originalPrice;
    private Integer quantityAvailable;
    private OfferStatus status;


    private Long sellerId;
    private List<String> featureNames;
    private List<Long> categoryIds;

    // For discounts
    private Double discountPercentage;
    private LocalDateTime discountExpiration;
}
