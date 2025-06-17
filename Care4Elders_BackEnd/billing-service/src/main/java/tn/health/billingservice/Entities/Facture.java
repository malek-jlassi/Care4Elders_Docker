package tn.health.billingservice.Entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "factures")
public class Facture {
    @Id
    private String id;
    private String careRequestId;
    private String patientId;
    private String description;
    private double amount;
    private LocalDateTime dateIssued;
    private boolean paid;
}
