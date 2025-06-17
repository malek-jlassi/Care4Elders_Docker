package tn.health.deliveryservice.Entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProductBillRequest {
    private String productId;
    private String nomProduit;
    private String descriptionProduit;
    private String region;
    private double prix;
    private int qt;
    private String image;
    private String status;

    // Custom validation method
    public void validate() {
        if (prix <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0");
        }
        if (qt <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        if (region == null || region.trim().isEmpty()) {
            throw new IllegalArgumentException("Region is required");
        }
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID is required");
        }
    }
} 