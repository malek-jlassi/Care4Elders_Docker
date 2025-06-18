package tn.health.deliveryservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import tn.health.deliveryservice.Entities.*;
import tn.health.deliveryservice.dto.DeliveryBillRequest;
import tn.health.deliveryservice.repository.DeliveryBillRepository;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DeliveryService {
    private static final double COST_PER_KM = 0.800; // Cost in DT per kilometer
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final DeliveryBillRepository deliveryBillRepository;

    @Autowired
    public DeliveryService(DeliveryBillRepository deliveryBillRepository) {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        this.deliveryBillRepository = deliveryBillRepository;
    }

    public double calculateDeliveryCost(String region) throws Exception {
        DistanceDetails details = calculateDistanceDetails(region);
        return details.getDeliveryCost();
    }

    public double calculateDistanceBetweenRegions(String fromRegion, String toRegion) throws Exception {
        Location fromLocation = getRegionLocation(fromRegion);
        Location toLocation = getRegionLocation(toRegion);
        
        return calculateDistance(
            fromLocation.getLatitude(),
            fromLocation.getLongitude(),
            toLocation.getLatitude(),
            toLocation.getLongitude()
        );
    }

    public DistanceDetails calculateDistanceDetails(String region) throws Exception {
        // Get current location coordinates
        Location currentLocation = getCurrentLocation();
        
        // Get destination coordinates from region
        Location destinationLocation = getRegionLocation(region);
        
        // Calculate distance
        double distance = calculateDistance(
            currentLocation.getLatitude(), 
            currentLocation.getLongitude(),
            destinationLocation.getLatitude(), 
            destinationLocation.getLongitude()
        );
        
        double deliveryCost = calculateDeliveryCostFromDistance(distance);
        
        return new DistanceDetails(
            distance,
            deliveryCost,
            currentLocation,
            destinationLocation
        );
    }

    public DeliveryBill generateBill(DeliveryBillRequest request) {
        log.info("Generating bill for request: {}", request);
        try {
            DeliveryBill bill = new DeliveryBill();
            bill.setUser(request.getUser());
            bill.setRegion(request.getRegion());
            bill.setDistanceInKm(request.getDistanceInKm());
            bill.setOrderItems(request.getOrderItems());
            bill.setTotalAmount(calculateTotalAmount(request.getOrderItems()));
            bill.setStatus(PaymentStatus.PENDING);
            bill.setCreatedAt(LocalDateTime.now());
            DeliveryBill savedBill = deliveryBillRepository.save(bill);
            log.info("Successfully generated bill with ID: {}", savedBill.getId());
            return savedBill;
        } catch (Exception e) {
            log.error("Error generating bill: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate bill: " + e.getMessage());
        }
    }

    private double calculateTotalAmount(List<OrderItem> orderItems) {
        return orderItems.stream()
            .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
            .sum();
    }

    public List<DeliveryBill> getBillsByUser(String userId) {
        return deliveryBillRepository.findByUserId(userId);
    }

    public List<DeliveryBill> getBillsByStatus(PaymentStatus status) {
        return deliveryBillRepository.findByStatus(status);
    }

    public List<DeliveryBill> getBillsByRegion(String region) {
        return deliveryBillRepository.findByRegion(region);
    }

    public DeliveryBill updateBillStatus(String billId, PaymentStatus newStatus) {
        DeliveryBill bill = deliveryBillRepository.findById(billId)
            .orElseThrow(() -> new RuntimeException("Bill not found"));
        bill.setStatus(newStatus);
        return deliveryBillRepository.save(bill);
    }

    private Location getCurrentLocation() throws Exception {
        String publicIp = getPublicIpAddress();
        String apiUrl = "http://ip-api.com/json/" + publicIp;
        
        JsonNode response = restTemplate.getForObject(apiUrl, JsonNode.class);
        
        return new Location(
            response.get("lat").asDouble(),
            response.get("lon").asDouble()
        );
    }

    private String getPublicIpAddress() throws Exception {
        URL url = new URL("http://checkip.amazonaws.com");
        return restTemplate.getForObject(url.toString(), String.class).trim();
    }

    private Location getRegionLocation(String region) throws Exception {
        String encodedRegion = java.net.URLEncoder.encode(region, "UTF-8");
        String apiUrl = "https://nominatim.openstreetmap.org/search?q=" + encodedRegion + "&format=json";
        
        JsonNode[] response = restTemplate.getForObject(apiUrl, JsonNode[].class);
        
        if (response != null && response.length > 0) {
            return new Location(
                response[0].get("lat").asDouble(),
                response[0].get("lon").asDouble()
            );
        }
        
        throw new Exception("Could not find coordinates for region: " + region);
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth's radius in kilometers

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c; // Distance in kilometers
    }

    public double calculateDeliveryCostFromDistance(double distance) {
        return distance * COST_PER_KM;
    }

    public DeliveryBill getBillById(String billId) {
        return deliveryBillRepository.findById(billId)
            .orElseThrow(() -> new RuntimeException("Bill not found with ID: " + billId));
    }

    public byte[] generateDeliveryBillPdf(String billId) throws DocumentException {
        log.info("Starting PDF generation for bill ID: {}", billId);
        try {
            DeliveryBill bill = deliveryBillRepository.findById(billId)
                .orElseThrow(() -> new RuntimeException("Bill not found with ID: " + billId));
            if (bill.getUser() == null) {
                throw new IllegalStateException("Bill has no associated user");
            }
            if (bill.getOrderItems() == null || bill.getOrderItems().isEmpty()) {
                throw new IllegalStateException("Bill has no order items");
            }
            Document document = new Document(PageSize.A4);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);
            document.open();
            // Add title
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, BaseColor.DARK_GRAY);
            Paragraph title = new Paragraph("Care4Elders - Facture de Livraison", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(30f);
            document.add(title);
            // Add bill details
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
            Font boldFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            document.add(new Paragraph("Numéro de Facture: " + bill.getId(), boldFont));
            document.add(new Paragraph("Date: " + bill.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), normalFont));
            document.add(new Paragraph("Status: " + bill.getStatus(), normalFont));
            document.add(new Paragraph("\n"));
            // Add client details
            document.add(new Paragraph("Détails du Client", boldFont));
            document.add(new Paragraph("Nom: " + bill.getUser().getName(), normalFont));
            document.add(new Paragraph("Email: " + bill.getUser().getEmail(), normalFont));
            document.add(new Paragraph("Téléphone: " + bill.getUser().getPhone(), normalFont));
            document.add(new Paragraph("\n"));
            // Add delivery details
            document.add(new Paragraph("Détails de Livraison", boldFont));
            document.add(new Paragraph("Région: " + bill.getRegion(), normalFont));
            document.add(new Paragraph("Distance: " + bill.getDistanceInKm() + " km", normalFont));
            document.add(new Paragraph("\n"));
            // Add products table
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);
            // Add table headers
            String[] headers = {"Produit", "Quantité", "Prix Unitaire", "Total"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, boldFont));
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setBorderWidth(2);
                cell.setPadding(5);
                table.addCell(cell);
            }
            // Add product rows
            for (OrderItem item : bill.getOrderItems()) {
                if (item.getProduct() == null) {
                    log.warn("Skipping order item with null product in bill {}", billId);
                    continue;
                }
                table.addCell(new Phrase(item.getProduct().getName(), normalFont));
                table.addCell(new Phrase(String.valueOf(item.getQuantity()), normalFont));
                table.addCell(new Phrase(String.format("%.2f TND", item.getProduct().getPrice()), normalFont));
                table.addCell(new Phrase(String.format("%.2f TND", item.getQuantity() * item.getProduct().getPrice()), normalFont));
            }
            document.add(table);
            // Add total
            document.add(new Paragraph("\n"));
            Paragraph total = new Paragraph(
                String.format("Total: %.2f TND", bill.getTotalAmount()),
                boldFont
            );
            total.setAlignment(Element.ALIGN_RIGHT);
            document.add(total);
            // Add footer
            document.add(new Paragraph("\n\n"));
            Paragraph footer = new Paragraph(
                "Merci de votre confiance!\nCare4Elders - Prendre soin de vos proches",
                new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC)
            );
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);
            document.close();
            log.info("Successfully generated PDF for bill ID: {}", billId);
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Error generating PDF for bill {}: {}", billId, e.getMessage(), e);
            throw new RuntimeException("Failed to generate PDF: " + e.getMessage());
        }
    }
}