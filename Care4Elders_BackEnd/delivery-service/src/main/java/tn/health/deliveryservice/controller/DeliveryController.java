package tn.health.deliveryservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import tn.health.deliveryservice.service.DeliveryService;
import tn.health.deliveryservice.Entities.*;
import java.time.LocalDateTime;

@RestController
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8080"}, allowCredentials = "true")
@RequestMapping("/api/delivery")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @Autowired
    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @GetMapping("/calculate-cost")
    public ResponseEntity<?> calculateDeliveryCost(
            @RequestParam String region,
            @RequestParam double productPrice) {
        try {
            DistanceDetails details = deliveryService.calculateDistanceDetails(region);
            double totalCost = productPrice + details.getDeliveryCost();
            
            return ResponseEntity.ok(new DeliveryCostResponse(
                details.getDeliveryCost(),
                productPrice,
                totalCost
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/calculate-distance")
    public ResponseEntity<DistanceResponse> calculateDistance(
            @RequestParam String fromRegion,
            @RequestParam String toRegion) {
        try {
            double distance = deliveryService.calculateDistanceBetweenRegions(fromRegion, toRegion);
            double estimatedCost = deliveryService.calculateDeliveryCostFromDistance(distance);
            DistanceResponse response = new DistanceResponse(
                fromRegion,
                toRegion,
                distance,
                estimatedCost
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new DistanceResponse(fromRegion, toRegion, 0.0, 0.0));
        }
    }

    @GetMapping("/distance-details")
    public ResponseEntity<?> getDistanceDetails(
            @RequestParam String region) {
        try {
            DistanceDetails details = deliveryService.calculateDistanceDetails(region);
            return ResponseEntity.ok(details);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/generate-bill")
    public ResponseEntity<?> generateBill(
            @RequestBody ProductBillRequest request,
            @RequestParam String userId) {
        try {
            // Validate request
            if (request == null) {
                return ResponseEntity.badRequest().body("Request body cannot be null");
            }

            // Log incoming request
            System.out.println("Received bill request: " + request);
            System.out.println("User ID: " + userId);

            // Validate price
            if (request.getPrix() <= 0) {
                return ResponseEntity.badRequest().body("Price must be greater than 0. Received: " + request.getPrix());
            }

            // Validate quantity
            if (request.getQt() <= 0) {
                return ResponseEntity.badRequest().body("Quantity must be greater than 0. Received: " + request.getQt());
            }

            // Validate user ID
            if (userId == null || userId.trim().isEmpty() || "anonymous".equalsIgnoreCase(userId)) {
                return ResponseEntity.badRequest().body("Valid user ID is required. Received: " + userId);
            }

            // Generate and save bill
            DeliveryBill bill = deliveryService.generateAndSaveBill(
                request.getProductId(),
                request.getNomProduit(),
                request.getDescriptionProduit(),
                request.getRegion(),
                request.getPrix(),
                request.getQt(),
                request.getImage(),
                request.getStatus(),
                userId
            );

            // Log generated bill
            System.out.println("Generated bill: " + bill);

            return ResponseEntity.ok(bill);
        } catch (IllegalArgumentException e) {
            System.err.println("Validation error: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            System.err.println("Error generating bill: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error generating bill: " + e.getMessage());
        }
    }

    @GetMapping("/bills/user/{userId}")
    public ResponseEntity<?> getUserBills(@PathVariable String userId) {
        return ResponseEntity.ok(deliveryService.getBillsByUser(userId));
    }

    @GetMapping("/bills/status/{status}")
    public ResponseEntity<?> getBillsByStatus(@PathVariable DeliveryStatus status) {
        return ResponseEntity.ok(deliveryService.getBillsByStatus(status));
    }

    @GetMapping("/bills/region/{region}")
    public ResponseEntity<?> getBillsByRegion(@PathVariable String region) {
        return ResponseEntity.ok(deliveryService.getBillsByRegion(region));
    }

    @PutMapping("/bills/{billId}/status")
    public ResponseEntity<?> updateBillStatus(
            @PathVariable String billId,
            @RequestParam DeliveryStatus status) {
        try {
            DeliveryBill updatedBill = deliveryService.updateBillStatus(billId, status);
            return ResponseEntity.ok(updatedBill);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/bills/{billId}/pdf")
    public ResponseEntity<?> downloadDeliveryBillPdf(@PathVariable String billId) {
        try {
            if (billId == null || billId.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Bill ID cannot be null or empty");
            }

            DeliveryBill bill = deliveryService.getBillById(billId);
            if (bill == null) {
                return ResponseEntity.notFound().build();
            }

            byte[] pdfContent = deliveryService.generateDeliveryBillPdf(bill);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "facture-" + billId + ".pdf");
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(pdfContent);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to generate PDF: " + e.getMessage());
        }
    }
} 