package tn.health.billingservice.Services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.health.billingservice.Entities.Facture;
import tn.health.billingservice.Repositories.FactureRepository;
import tn.health.billingservice.dto.CareRequestPayload;

import java.time.LocalDateTime;
import java.util.List;
@Service
@AllArgsConstructor
public class FactureService implements IFactureService {

    private final FactureRepository factureRepository;

    @Override
    public Facture generateFromCareRequest(CareRequestPayload careRequest) {
        double amount = calculateAmount(careRequest.careType()); // Use string

        Facture facture = new Facture();
        facture.setCareRequestId(careRequest.id());
        facture.setPatientId(careRequest.patientId()); // Flat access
        facture.setDescription("Billing for care: " + careRequest.careType());
        facture.setAmount(amount);
        facture.setDateIssued(LocalDateTime.now());
        facture.setPaid(false);
        return factureRepository.save(facture);
    }

    private double calculateAmount(String careType) {
        return switch (careType.toUpperCase()) {
            case "REPAS" -> 15.0;
            case "BAIN" -> 20.0;
            case "MENAGE" -> 25.0;
            case "VISITE_MEDECIN" -> 50.0;
            default -> 10.0; // fallback value
        };
    }


    @Override
    public List<Facture> getByPatient(String patientId) {
        return factureRepository.findByPatientId(patientId);
    }

    @Override
    public Facture findById(String id) {
        return factureRepository.findById(id).orElse(null);
    }
}
