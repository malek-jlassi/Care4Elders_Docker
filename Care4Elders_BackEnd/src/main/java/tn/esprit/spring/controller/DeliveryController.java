package tn.esprit.spring.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import tn.esprit.spring.dto.DeliveryBillRequest;
import tn.esprit.spring.entity.DeliveryBill;
import tn.esprit.spring.entity.PaymentStatus;
import tn.esprit.spring.service.DeliveryService;
import org.springframework.http.HttpStatus;
import java.util.Map;

@RestController
@RequestMapping("/api/delivery")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@Slf4j
public class DeliveryController {
    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @PostMapping("/generate-bill")
    public ResponseEntity<?> generateBill(@RequestBody DeliveryBillRequest request) {
        log.info("Received request to generate bill");
        try {
            DeliveryBill bill = deliveryService.generateBill(request);
            return ResponseEntity.ok(bill);
        } catch (Exception e) {
            log.error("Error generating bill: {}", e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to generate bill: " + e.getMessage());
        }
    }

    @PutMapping("/bills/{billId}/payment")
    public ResponseEntity<?> updatePaymentStatus(
            @PathVariable String billId,
            @RequestParam String paymentId,
            @RequestParam PaymentStatus status) {
        log.info("Received request to update payment status for bill ID: {}", billId);
        try {
            DeliveryBill bill = deliveryService.updatePaymentStatus(billId, paymentId, status);
            return ResponseEntity.ok(bill);
        } catch (Exception e) {
            log.error("Error updating payment status: {}", e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to update payment status: " + e.getMessage());
        }
    }

    // Test endpoint to check if the controller is accessible
    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("Delivery service is working!");
    }

    @GetMapping("/bills/{billId}/verify")
    public ResponseEntity<?> verifyBillExists(@PathVariable String billId) {
        log.info("Received request to verify bill ID: {}", billId);
        try {
            boolean exists = deliveryService.verifyBillExists(billId);
            return ResponseEntity.ok()
                .body(Map.of(
                    "exists", exists,
                    "billId", billId
                ));
        } catch (Exception e) {
            log.error("Error verifying bill: {}", e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error verifying bill: " + e.getMessage());
        }
    }

    @GetMapping("/bills/{billId}/pdf")
    public ResponseEntity<?> downloadDeliveryBillPdf(@PathVariable String billId) {
        log.info("Received request to generate PDF for bill ID: {}", billId);
        
        try {
            if (billId == null || billId.trim().isEmpty()) {
                log.error("Invalid bill ID provided");
                return ResponseEntity
                    .badRequest()
                    .body("Bill ID cannot be null or empty");
            }

            log.info("Attempting to generate PDF for bill ID: {}", billId);
            byte[] pdfContent = deliveryService.generateDeliveryBillPdf(billId);
            
            if (pdfContent == null || pdfContent.length == 0) {
                log.error("Generated PDF content is empty for bill ID: {}", billId);
                return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Generated PDF content is empty");
            }
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "facture-" + billId + ".pdf");
            
            log.info("Successfully generated PDF for bill ID: {} with size: {} bytes", billId, pdfContent.length);
            return ResponseEntity.ok()
                .headers(headers)
                .body(pdfContent);
        } catch (Exception e) {
            log.error("Error generating PDF for bill {}: {}", billId, e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to generate PDF: " + e.getMessage());
        }
    }
} 