package tn.health.billingservice.RestControllers;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.health.billingservice.Entities.Facture;
import tn.health.billingservice.Services.IFactureService;
import tn.health.billingservice.dto.CareRequestPayload;

import java.util.List;

@RestController
@RequestMapping("/factures")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class FactureRestController {

    private final IFactureService factureService;

    @PostMapping("/generate")
    public ResponseEntity<Facture> generate(@RequestBody CareRequestPayload careRequest) {
        return ResponseEntity.ok(factureService.generateFromCareRequest(careRequest));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Facture>> getByPatient(@PathVariable String patientId) {
        return ResponseEntity.ok(factureService.getByPatient(patientId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Facture> getFactureById(@PathVariable String id) {
        Facture facture = factureService.findById(id);
        if (facture != null) {
            return ResponseEntity.ok(facture);
        } else {
            return ResponseEntity.notFound().build();
        }
    }  // --- PDF Download Endpoint ---
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadFacturePdf(@PathVariable String id) {
        // TODO: Replace with real PDF generation logic
        // For demonstration, return a simple PDF file with dummy content
        try {
            // Dummy PDF bytes (minimal valid PDF header)
            byte[] pdfBytes = "%PDF-1.4\n1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n2 0 obj\n<< /Type /Pages /Kids [3 0 R] /Count 1 >>\nendobj\n3 0 obj\n<< /Type /Page /Parent 2 0 R /MediaBox [0 0 200 200] /Contents 4 0 R >>\nendobj\n4 0 obj\n<< /Length 44 >>\nstream\nBT /F1 24 Tf 50 150 Td (Facture PDF Demo) Tj ET\nendstream\nendobj\nxref\n0 5\n0000000000 65535 f \n0000000010 00000 n \n0000000079 00000 n \n0000000178 00000 n \n0000000297 00000 n \ntrailer\n<< /Root 1 0 R /Size 5 >>\nstartxref\n410\n%%EOF".getBytes();
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=facture-" + id + ".pdf")
                    .header("Content-Type", "application/pdf")
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}



