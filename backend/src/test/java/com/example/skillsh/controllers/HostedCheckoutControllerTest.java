package com.example.skillsh.controllers;

import com.example.skillsh.domain.entity.ServiceOffer;
import com.example.skillsh.domain.entity.ServiceOrder;
import com.example.skillsh.domain.entity.User;
import com.example.skillsh.repository.ServiceOfferRepository;
import com.example.skillsh.repository.ServiceOrderRepository;
import com.example.skillsh.repository.UserRepo;
import com.example.skillsh.web.HostedCheckoutController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HostedCheckoutController.class)
@AutoConfigureMockMvc(addFilters = false)
class HostedCheckoutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserRepo userRepository;
    @MockBean
    private ServiceOfferRepository offerRepository;
    @MockBean
    private ServiceOrderRepository orderRepository;

    // Секюрити фикс
    @MockBean
    private UserDetailsService userDetailsService;

    // 1. Тест за създаване на сесия (Проверяваме дали обработва правилно грешките от Stripe)
    @Test
    void testCreateSession_WhenStripeApiFails_ShouldReturn500() throws Exception {
        // Arrange
        // ПРОМЯНА 1: Подаваме userId, както очаква CheckoutRequest
        Map<String, Object> payload = Map.of(
                "userId", 1,
                "offerId", 1
        );

        User buyer = new User();
        buyer.setId(1L);

        ServiceOffer offer = new ServiceOffer();
        offer.setId(1L);
        offer.setName("Урок по пиано");
        offer.setPrice(BigDecimal.valueOf(50.00));

        ServiceOrder savedOrder = new ServiceOrder();
        savedOrder.setId(100L); // Трябва ни ID, защото се ползва в .putMetadata("order_id", ...)

        // ПРОМЯНА 2: Сървисът търси по ID, затова мокваме findById
        when(userRepository.findById(1L)).thenReturn(Optional.of(buyer));
        when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));
        when(orderRepository.save(any(ServiceOrder.class))).thenReturn(savedOrder);

        // Act & Assert
        // ПРОМЯНА 3: Точният URL път от контролера
        mockMvc.perform(post("/api/hostedCheckout/create-checkout-session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isInternalServerError());
    }

    // 2. Тест за Success страницата (връща HTML)
    @Test
    void testSuccessPage_ShouldReturnHtml() throws Exception {
        mockMvc.perform(get("/api/hostedCheckout/success")
                        .param("session_id", "cs_test_123456"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Payment Successful!")));
    }

    // 3. Тест за Cancel страницата (връща HTML)
    @Test
    void testCancelPage_ShouldReturnHtml() throws Exception {
        mockMvc.perform(get("/api/hostedCheckout/cancel"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Payment Cancelled")));
    }
}


