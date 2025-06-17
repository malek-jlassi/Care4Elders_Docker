package tn.health.deliveryservice.Entities;

public enum DeliveryStatus {
    PENDING,           // Initial state when the delivery is created
    CONFIRMED,         // Delivery has been confirmed by the service
    PICKED_UP,        // Package has been picked up by delivery person
    IN_TRANSIT,       // Package is on the way to destination
    OUT_FOR_DELIVERY, // Package is out for final delivery
    DELIVERED,        // Successfully delivered to destination
    FAILED,           // Delivery attempt failed
    CANCELLED,        // Delivery was cancelled
    RETURNED          // Package was returned to sender
} 