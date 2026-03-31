package com.example.skillsh.controllers;

import com.example.skillsh.domain.entity.ServiceOffer;
import com.example.skillsh.domain.entity.ServiceOrder;
import com.example.skillsh.domain.entity.User;
import jakarta.servlet.ServletException;
import com.example.skillsh.repository.ServiceOfferRepository;
import com.example.skillsh.repository.ServiceOrderRepository;
import com.example.skillsh.repository.UserRepo;
import com.example.skillsh.services.payment.StripeService;
import com.example.skillsh.web.PaymentController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.NestedServletException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StripeService stripeService;
    @MockBean
    private UserRepo userRepository;
    @MockBean
    private ServiceOfferRepository offerRepository;
    @MockBean
    private ServiceOrderRepository orderRepository;

    // Секюрити фикс
    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void testCreatePaymentIntent_StripeApiFails() throws Exception {
        // Arrange
        PaymentController.PaymentRequest request = new PaymentController.PaymentRequest();
        request.setBuyerId(1L);
        request.setOfferId(2L);

        User buyer = new User();
        buyer.setId(1L);

        ServiceOffer offer = new ServiceOffer();
        offer.setId(2L);
        offer.setName("Курс по програмиране");
        offer.setPrice(BigDecimal.valueOf(100));

        ServiceOrder order = new ServiceOrder();
        order.setId(100L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(buyer));
        when(offerRepository.findById(2L)).thenReturn(Optional.of(offer));
        when(orderRepository.save(any(ServiceOrder.class))).thenReturn(order);

        // Act & Assert
        // ТУК Е ПРОМЯНАТА: Очакваме ServletException вместо NestedServletException
        assertThrows(ServletException.class, () -> {
            mockMvc.perform(post("/api/payment/create-payment-intent")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));
        });

        // Проверяваме дали все пак сме стигнали до запазването на поръчката преди Stripe да гръмне
        verify(orderRepository, times(1)).save(any(ServiceOrder.class));
    }
}
