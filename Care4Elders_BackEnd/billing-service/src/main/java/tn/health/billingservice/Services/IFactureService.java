package tn.health.billingservice.Services;

import tn.health.billingservice.Entities.Facture;
import tn.health.billingservice.dto.CareRequestPayload;
import java.util.List;

public interface IFactureService {
    Facture generateFromCareRequest(CareRequestPayload careRequest);
    List<Facture> getByPatient(String patientId);
    Facture findById(String id);
}


