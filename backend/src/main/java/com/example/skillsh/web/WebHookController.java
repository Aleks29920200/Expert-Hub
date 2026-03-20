package com.example.skillsh.web;

import com.example.skillsh.domain.dto.review.AddReviewRequest;
import com.example.skillsh.services.payment.ServiceOrderService;
import com.example.skillsh.services.review.ReviewService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import com.stripe.model.Event;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhooks/stripe")
public class WebHookController {
    // WARNING: Do not hardcode the secret. Use @Value("${stripe.webhook.secret}") and application.properties
    @Value("whsec_3ae05d3022abb68659d8d5fd69a3d549aaa0b02cf3171af21a1b7657e9f02f39")
    private String webhookSecret;
    private final ServiceOrderService serviceOrderService;

    public WebHookController(@Value("whsec_3ae05d3022abb68659d8d5fd69a3d549aaa0b02cf3171af21a1b7657e9f02f39") String webhookSecret, ServiceOrderService serviceOrderService) {
        this.webhookSecret = webhookSecret; // Correct injection
        this.serviceOrderService = serviceOrderService;
    }

    @PostMapping
    public ResponseEntity<String> handleWebhookEvent(@RequestBody String payload,
                                                     @RequestHeader("Stripe-Signature") String header) {
        Event event;

        // --- STEP A: VERIFY SIGNATURE ---
        try {
            event = Webhook.constructEvent(
                    payload, header, webhookSecret
            );
        } catch (SignatureVerificationException e) {
            System.err.println("Webhook Signature Verification Failed: " + e.getMessage());
            // Fail validation immediately
            return new ResponseEntity<>("Invalid signature", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            System.err.println("Webhook Payload Parsing Failed: " + e.getMessage());
            return new ResponseEntity<>("Bad Request", HttpStatus.BAD_REQUEST);
        }

        // --- STEP B: PROCESS EVENT TYPE ---
        try {
            switch (event.getType()) {
                case "payment_intent.succeeded":
                case "payment_intent.payment_failed":
                    PaymentIntent intent = (PaymentIntent) event.getDataObjectDeserializer()
                            .getObject().orElseThrow(() -> new IllegalStateException("Missing PaymentIntent object."));

                    // Extract the CRITICAL 'order_id' from the metadata
                    String orderIdString = intent.getMetadata().get("order_id");
                    if (orderIdString == null) {
                        System.err.println("Metadata missing 'order_id' for PaymentIntent: " + intent.getId());
                        // Acknowledge the event but log the error
                        return new ResponseEntity<>("ok", HttpStatus.OK);
                    }
                    Long orderId = Long.valueOf(orderIdString);

                    String status;
                    if (event.getType().equals("payment_intent.succeeded")) {
                        status = "PAID";
                        System.out.println("✅ Payment succeeded for Order ID: " + orderId);
                    } else {
                        status = "FAILED";
                        System.out.println("❌ Payment failed for Order ID: " + orderId);
                    }

                    // CORE LOGIC: Update DB status using the ServiceOrder ID
                    serviceOrderService.updateOrderStatus(orderId, status);
                    break;

                case "checkout.session.completed":
                    // If using Checkout Sessions, retrieve PaymentIntent ID or metadata from the session
                    var session = (com.stripe.model.checkout.Session) event.getDataObjectDeserializer()
                            .getObject().orElseThrow(() -> new IllegalStateException("Missing Session object."));

                    if (session.getPaymentStatus().equals("paid")) {
                        // Option 1: Retrieve PaymentIntent and use its metadata (recommended)
                        String piId = session.getPaymentIntent();
                        if (piId != null) {
                            PaymentIntent pi = PaymentIntent.retrieve(piId);
                            String orderIdFromSessionPi = pi.getMetadata().get("order_id");
                            if (orderIdFromSessionPi != null) {
                                serviceOrderService.updateOrderStatus(Long.valueOf(orderIdFromSessionPi), "PAID");
                                System.out.println("Checkout Session Completed, Order ID: " + orderIdFromSessionPi);
                            }
                        }
                    }
                    break;

                default:
                    System.out.println("Unhandled event type: " + event.getType());
            }

            // --- STEP C: ACKNOWLEDGE RECEIPT ---
            return new ResponseEntity<>("ok", HttpStatus.OK);

        } catch (Exception e) {
            System.err.println("Internal Server Error processing Stripe event: " + e.getMessage());
            // Return 500 so Stripe retries the webhook
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}


