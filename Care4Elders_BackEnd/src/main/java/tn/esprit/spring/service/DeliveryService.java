import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import tn.esprit.spring.entity.*;
import tn.esprit.spring.repository.DeliveryBillRepository;
import javax.persistence.EntityNotFoundException;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class DeliveryService {
    @Autowired
    private DeliveryBillRepository deliveryBillRepository;

    public DeliveryBill generateBill(DeliveryBillRequest request) {
        log.info("Generating bill for request: {}", request);
        
        try {
            DeliveryBill bill = new DeliveryBill();
            bill.setUser(request.getUser());
            bill.setRegion(request.getRegion());
            bill.setDistanceInKm(request.getDistanceInKm());
            bill.setOrderItems(request.getOrderItems());
            bill.setTotalAmount(calculateTotalAmount(request.getOrderItems()));
            bill.setStatus(PaymentStatus.PENDING.toString());
            bill.setCreatedAt(LocalDateTime.now());
            
            DeliveryBill savedBill = deliveryBillRepository.save(bill);
            log.info("Successfully generated bill with ID: {}", savedBill.getId());
            return savedBill;
        } catch (Exception e) {
            log.error("Error generating bill: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate bill: " + e.getMessage());
        }
    }

    public DeliveryBill updatePaymentStatus(String billId, String paymentId, PaymentStatus status) {
        log.info("Updating payment status for bill ID: {} with payment ID: {} to status: {}", billId, paymentId, status);
        
        try {
            DeliveryBill bill = deliveryBillRepository.findById(billId)
                .orElseThrow(() -> new EntityNotFoundException("Bill not found with ID: " + billId));
            
            bill.setPaymentId(paymentId);
            bill.setStatus(status.toString());
            
            DeliveryBill updatedBill = deliveryBillRepository.save(bill);
            log.info("Successfully updated payment status for bill ID: {}", billId);
            return updatedBill;
        } catch (Exception e) {
            log.error("Error updating payment status: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update payment status: " + e.getMessage());
        }
    }

    private double calculateTotalAmount(List<OrderItem> orderItems) {
        return orderItems.stream()
            .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
            .sum();
    }

    public boolean verifyBillExists(String billId) {
        log.info("Verifying existence of bill with ID: {}", billId);
        boolean exists = deliveryBillRepository.existsById(billId);
        log.info("Bill {} {}", billId, exists ? "exists" : "does not exist");
        return exists;
    }

    public byte[] generateDeliveryBillPdf(String billId) throws DocumentException {
        log.info("Starting PDF generation for bill ID: {}", billId);
        
        try {
            // First verify if bill exists
            if (!verifyBillExists(billId)) {
                throw new EntityNotFoundException("Bill not found with ID: " + billId);
            }

            DeliveryBill bill = deliveryBillRepository.findById(billId)
                .orElseThrow(() -> new EntityNotFoundException("Bill not found with ID: " + billId));

            log.info("Retrieved bill data for ID: {}", billId);

            // Validate bill data
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

            log.info("Started creating PDF document");

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
        } catch (EntityNotFoundException e) {
            log.error("Bill not found: {}", e.getMessage());
            throw e;
        } catch (IllegalStateException e) {
            log.error("Invalid bill data: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error generating PDF for bill {}: {}", billId, e.getMessage(), e);
            throw new RuntimeException("Failed to generate PDF: " + e.getMessage());
        }
    }
} 