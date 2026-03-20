package com.example.skillsh.web;

import com.example.skillsh.domain.entity.ServiceOffer;
import com.example.skillsh.domain.entity.ServiceOrder;
import com.example.skillsh.domain.entity.User;
import com.example.skillsh.repository.ServiceOfferRepository;
import com.example.skillsh.repository.ServiceOrderRepository;
import com.example.skillsh.repository.UserRepo;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.view.RedirectView;
import java.util.Map;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/hostedCheckout")
@Slf4j
public class HostedCheckoutController {

    private final UserRepo userRepository;
    private final ServiceOfferRepository offerRepository;
    private final ServiceOrderRepository orderRepository;

    // Use property injection for dynamic base URL
    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public HostedCheckoutController(UserRepo userRepository, ServiceOfferRepository offerRepository, ServiceOrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.offerRepository = offerRepository;
        this.orderRepository = orderRepository;
    }

    // A simple DTO to accept parameters for the checkout
    public static class CheckoutRequest {
        @NotNull
        public Long userId;
        @NotNull
        public Long offerId;
    }

    /**
     * Creates a ServiceOrder, then a Stripe Checkout Session, and redirects the user.
     * * @param request Contains userId and offerId to create the ServiceOrder.
     * @return RedirectView to the Stripe Hosted Checkout page.
     */
    // ДОБАВИ ТОЗИ ИМПОРТ НАГОРЕ ПРИ ДРУГИТЕ:
    // import java.util.Map;

    @PostMapping("/create-checkout-session")
    public ResponseEntity<?> checkout(@RequestBody CheckoutRequest request) throws StripeException {
        // --- 1. Fetch User and Service Offer ---
        User buyer = userRepository.findById(request.userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        ServiceOffer offer = offerRepository.findById(request.offerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service Offer not found"));

        long amountInCents = offer.getPrice().movePointRight(2).longValueExact();
        String currency = "usd";

        // --- 2. Create and Save PENDING Service Order ---
        ServiceOrder newOrder = new ServiceOrder();
        newOrder.setBuyer(buyer);
        newOrder.setOffer(offer);
        newOrder.setAmount(offer.getPrice());
        newOrder.setCurrency(currency);
        newOrder.setOrderDate(LocalDateTime.now());
        newOrder.setStatus("PENDING");

        ServiceOrder savedOrder = orderRepository.save(newOrder);
        log.info("New Service Order {} created as PENDING for Hosted Checkout.", savedOrder.getId());

        try {
            // --- 3. Define the Session Creation Parameters ---
            SessionCreateParams params =
                    SessionCreateParams.builder()
                            .setMode(SessionCreateParams.Mode.PAYMENT)
                            .setSuccessUrl(baseUrl + "/hostedCheckout/success?session_id={CHECKOUT_SESSION_ID}")
                            .setCancelUrl(baseUrl + "/hostedCheckout/cancel")
                            .putMetadata("order_id", String.valueOf(savedOrder.getId()))
                            .setPaymentIntentData(
                                    SessionCreateParams.PaymentIntentData.builder()
                                            .putMetadata("order_id", String.valueOf(savedOrder.getId()))
                                            .build())
                            .addLineItem(
                                    SessionCreateParams.LineItem.builder()
                                            .setQuantity(1L)
                                            .setPriceData(
                                                    SessionCreateParams.LineItem.PriceData.builder()
                                                            .setCurrency(currency)
                                                            .setUnitAmount(amountInCents)
                                                            .setProductData(
                                                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                            .setName(offer.getName())
                                                                            .setDescription("Service Offer #" + offer.getId())
                                                                            .build())
                                                            .build())
                                            .build())
                            .build();

            // 5. Create the Session
            Session session = Session.create(params);

            // ПРОМЯНАТА Е ТУК: Връщаме JSON обект с URL-а към Stripe, вместо RedirectView!
            return ResponseEntity.ok(Map.of("url", session.getUrl()));

        } catch (StripeException e) {
            log.error("Error creating Stripe Checkout Session:", e);
            savedOrder.setStatus("FAILED_CREATION");
            orderRepository.save(savedOrder);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to initialize payment: " + e.getMessage());
        }
    }
    // --- Success and Cancel Handlers ---

    @GetMapping("/success")
    public ResponseEntity<String> successPage(@RequestParam("session_id") String sessionId) {
        log.info("Successful payment redirect for Stripe Session ID: {}", sessionId);
        // NOTE: Order status update must rely ONLY on the webhook, not this client-side redirect.
        return ResponseEntity.ok("<h1>Payment Successful!</h1><p>Thank you for your purchase. We are processing your order. Session: " + sessionId + "</p>");
    }

    @GetMapping("/cancel")
    public ResponseEntity<String> cancelPage() {
        // NOTE: This does not mean the order is failed; it only means the user quit the checkout flow.
        return ResponseEntity.ok("<h1>Payment Cancelled</h1><p>Your payment was not completed. You can try again.</p>");
    }
}