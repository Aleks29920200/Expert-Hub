package com.example.skillsh.repository;

import com.example.skillsh.domain.entity.ServiceOrder;
import com.example.skillsh.domain.entity.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ServiceOrderRepository extends JpaRepository<ServiceOrder, Long> {
    Optional<ServiceOrder> findByStripePaymentIntentId(String stripePaymentIntentId);

    boolean existsByBuyer_UsernameAndOffer_Seller_IdAndStatus(String buyer_username, Long offer_seller_id, String status);
}

