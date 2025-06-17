package tn.health.productservice.Entities;

import jakarta.persistence.Transient;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "produits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Produit {

    @Id
    String id;

    String nomProduit;  // 🟢 Nom extrait par OCR
    String descriptionProduit;
    String region;
    Double prix;
    Integer qt;  // 🔢 Détermine dynamiquement le statut
    String image;

    // 🔴 Supprimer le champ `StatusProduit`

    @Transient
    public String getStatusProduit() {
        return (qt == null || qt == 0) ? "RUPTURE_DE_STOCK" : "EN_STOCK";
    }
}