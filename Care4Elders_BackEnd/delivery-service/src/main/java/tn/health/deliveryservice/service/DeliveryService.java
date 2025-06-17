package tn.health.deliveryservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import tn.health.deliveryservice.Entities.*;
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

    public DeliveryBill generateAndSaveBill(String productId, String productName, String description, 
                                          String region, double price, int quantity, 
                                          String image, String status, String userId) throws Exception {
        // Log input parameters
        System.out.println("Generating bill with parameters:");
        System.out.println("Product ID: " + productId);
        System.out.println("Product Name: " + productName);
        System.out.println("Region: " + region);
        System.out.println("Price: " + price);
        System.out.println("Quantity: " + quantity);
        System.out.println("User ID: " + userId);

        // Validate inputs
        if (userId == null || userId.trim().isEmpty() || "anonymous".equalsIgnoreCase(userId)) {
            throw new IllegalArgumentException("Valid user ID is required");
        }

        if (price <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0, received: " + price);
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0, received: " + quantity);
        }

        if (region == null || region.trim().isEmpty()) {
            throw new IllegalArgumentException("Region is required");
        }

        // Calculate delivery details
        DistanceDetails distanceDetails = calculateDistanceDetails(region);
        
        // Calculate prices
        double totalProductPrice = price * quantity;
        double totalPrice = totalProductPrice + distanceDetails.getDeliveryCost();
        
        System.out.println("Calculated prices:");
        System.out.println("Total Product Price: " + totalProductPrice);
        System.out.println("Delivery Cost: " + distanceDetails.getDeliveryCost());
        System.out.println("Total Price: " + totalPrice);

        // Create bill
        DeliveryBill bill = new DeliveryBill();
        bill.setNomProduit(productName);
        bill.setDescriptionProduit(description);
        bill.setRegion(region);
        bill.setPrix(price);
        bill.setQt(quantity);
        bill.setImage(image);
        bill.setStatus(status);
        bill.setDistanceInKm(distanceDetails.getDistanceInKm());
        bill.setDeliveryCost(distanceDetails.getDeliveryCost());
        bill.setTotalPrice(totalPrice);
        bill.setUserLocation(distanceDetails.getUserLocation());
        bill.setDestinationLocation(distanceDetails.getDestinationLocation());
        bill.setCreatedAt(LocalDateTime.now());
        bill.setUserId(userId);
        bill.setOrderStatus(DeliveryStatus.PENDING);

        // Save and return
        DeliveryBill savedBill = deliveryBillRepository.save(bill);
        System.out.println("Saved bill: " + savedBill);
        return savedBill;
    }

    public List<DeliveryBill> getBillsByUser(String userId) {
        return deliveryBillRepository.findByUserId(userId);
    }

    public List<DeliveryBill> getBillsByStatus(DeliveryStatus status) {
        return deliveryBillRepository.findByOrderStatus(status);
    }

    public List<DeliveryBill> getBillsByRegion(String region) {
        return deliveryBillRepository.findByRegion(region);
    }

    public DeliveryBill updateBillStatus(String billId, DeliveryStatus newStatus) {
        DeliveryBill bill = deliveryBillRepository.findById(billId)
            .orElseThrow(() -> new RuntimeException("Bill not found"));
        bill.setOrderStatus(newStatus);
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

    public byte[] generateDeliveryBillPdf(DeliveryBill bill) throws Exception {
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

        // Add product details
        document.add(new Paragraph("Détails du Produit", boldFont));
        document.add(new Paragraph("Nom: " + bill.getNomProduit(), normalFont));
        document.add(new Paragraph("Description: " + bill.getDescriptionProduit(), normalFont));
        document.add(new Paragraph("Quantité: " + bill.getQt(), normalFont));
        document.add(new Paragraph("Prix unitaire: " + bill.getPrix() + " TND", normalFont));
        document.add(new Paragraph("\n"));

        // Add delivery details
        document.add(new Paragraph("Détails de Livraison", boldFont));
        document.add(new Paragraph("Région: " + bill.getRegion(), normalFont));
        document.add(new Paragraph("Distance: " + bill.getDistanceInKm() + " km", normalFont));
        document.add(new Paragraph("Frais de livraison: " + bill.getDeliveryCost() + " TND", normalFont));
        document.add(new Paragraph("\n"));

        // Add total
        document.add(new Paragraph("Résumé", boldFont));
        document.add(new Paragraph("Sous-total produit: " + (bill.getPrix() * bill.getQt()) + " TND", normalFont));
        document.add(new Paragraph("Frais de livraison: " + bill.getDeliveryCost() + " TND", normalFont));
        Paragraph total = new Paragraph("Total: " + bill.getTotalPrice() + " TND", boldFont);
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
        return baos.toByteArray();
    }
} 