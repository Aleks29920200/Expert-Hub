package com.example.skillsh.controllers;

import com.example.skillsh.services.payment.ServiceOrderService;
import com.example.skillsh.web.WebHookController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WebHookController.class)
@AutoConfigureMockMvc(addFilters = false)
class WebHookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Мокваме сървиса, който обработва поръчките след успешно плащане
    @MockBean
    private ServiceOrderService serviceOrderService;

    // Нашият верен спасител за Spring Security!
    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void testStripeWebhook_WithInvalidSignature_ShouldReturn400() throws Exception {
        // Arrange
        String fakePayload = "{\"id\": \"evt_fake123\", \"object\": \"event\"}";
        String fakeSignature = "t=12345,v1=fake_signature";

        // Act & Assert
        // Пращаме фалшив JSON и фалшив подпис.
        // Очакваме контролерът да разпознае измамата и да върне 400 Bad Request
        // със съобщение "Invalid signature".
        mockMvc.perform(post("/webhooks/stripe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(fakePayload)
                        .header("Stripe-Signature", fakeSignature))
                .andExpect(status().isBadRequest()) // ПРОМЯНА: Очакваме 400
                .andExpect(content().string("Invalid signature")); // ПРОМЯНА: Очакваме този текст
    }
}
