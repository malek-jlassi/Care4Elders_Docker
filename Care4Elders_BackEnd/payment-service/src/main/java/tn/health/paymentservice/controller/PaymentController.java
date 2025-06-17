package tn.health.paymentservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.health.paymentservice.service.PaymentService;
import com.stripe.model.PaymentIntent;
import lombok.extern.slf4j.Slf4j;
import lombok.Data;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8080"}, allowCredentials = "true")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Data
    public static class PaymentRequestDTO {
        private double amount;
    }

    @Data
    public static class PaymentResponseDTO {
        private String clientSecret;
        private String paymentId;
    }

    @PostMapping("/create-payment-intent")
    public ResponseEntity<?> createPaymentIntent(@RequestBody PaymentRequestDTO request) {
        try {
            log.info("Creating payment intent for amount: {}", request.getAmount());
            PaymentIntent intent = paymentService.createPaymentIntent(request.getAmount());
            
            PaymentResponseDTO response = new PaymentResponseDTO();
            response.setClientSecret(intent.getClientSecret());
            response.setPaymentId(intent.getId());
            
            log.info("Payment intent created successfully with id: {}", intent.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error creating payment intent: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/confirm-payment")
    public ResponseEntity<?> confirmPayment(@RequestBody Map<String, String> request) {
        try {
            String paymentIntentId = request.get("paymentIntentId");
            if (paymentIntentId == null || paymentIntentId.isEmpty()) {
                return ResponseEntity.badRequest().body("Payment intent ID is required");
            }

            PaymentIntent confirmedIntent = paymentService.confirmPayment(paymentIntentId);
            return ResponseEntity.ok(confirmedIntent);
        } catch (Exception e) {
            log.error("Error confirming payment: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/payment-status/{paymentId}")
    public ResponseEntity<?> getPaymentStatus(@PathVariable String paymentId) {
        try {
            log.info("Fetching payment status for paymentId: {}", paymentId);
            PaymentIntent intent = paymentService.retrievePaymentIntent(paymentId);
            return ResponseEntity.ok(new PaymentStatusDTO(intent.getStatus()));
        } catch (Exception e) {
            log.error("Error fetching payment status: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

class PaymentStatusDTO {
    private String status;

    public PaymentStatusDTO(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
} 