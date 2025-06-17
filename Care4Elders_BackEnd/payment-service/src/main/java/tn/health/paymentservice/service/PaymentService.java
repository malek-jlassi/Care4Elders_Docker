package tn.health.paymentservice.service;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class PaymentService {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    private static final double TND_TO_USD_RATE = 0.32; // Approximate conversion rate TND to USD

    @PostConstruct
    public void init() {
        log.info("Initializing Stripe with secret key");
        Stripe.apiKey = stripeSecretKey;
        try {
            // Test that the API key is valid
            PaymentIntent.list(new HashMap<>()).getData();
            log.info("Stripe initialization successful");
        } catch (Exception e) {
            log.error("Failed to initialize Stripe: {}", e.getMessage());
            throw new RuntimeException("Failed to initialize Stripe", e);
        }
    }

    public PaymentIntent createPaymentIntent(double amountTND) throws Exception {
        try {
            // Convert TND to USD cents (Stripe requires amount in cents)
            long amountUSDCents = Math.round(amountTND * TND_TO_USD_RATE * 100);
            
            log.info("Creating payment intent for amount: {} TND ({} USD cents)", amountTND, amountUSDCents);

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountUSDCents)
                .setCurrency("usd")
                .setAutomaticPaymentMethods(
                    PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                        .setEnabled(true)
                        .build()
                )
                .build();

            PaymentIntent intent = PaymentIntent.create(params);
            log.info("Payment intent created successfully with id: {}", intent.getId());
            return intent;
        } catch (Exception e) {
            log.error("Error creating payment intent: {}", e.getMessage());
            throw e;
        }
    }

    public PaymentIntent confirmPayment(String paymentIntentId) throws Exception {
        try {
            PaymentIntent intent = PaymentIntent.retrieve(paymentIntentId);
            Map<String, Object> params = new HashMap<>();
            params.put("payment_method", "pm_card_visa"); // For testing only
            
            PaymentIntent confirmedIntent = intent.confirm(params);
            log.info("Payment confirmed successfully for intent: {}", paymentIntentId);
            return confirmedIntent;
        } catch (Exception e) {
            log.error("Error confirming payment: {}", e.getMessage());
            throw e;
        }
    }

    public PaymentIntent retrievePaymentIntent(String paymentIntentId) throws Exception {
        try {
            log.info("Retrieving payment intent with ID: {}", paymentIntentId);
            return PaymentIntent.retrieve(paymentIntentId);
        } catch (Exception e) {
            log.error("Error retrieving payment intent: {}", e.getMessage(), e);
            throw e;
        }
    }

} 