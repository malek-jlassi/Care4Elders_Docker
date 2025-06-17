package tn.health.careservice.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import tn.health.careservice.dto.DeliveryRequestPayload;
import tn.health.careservice.dto.DeliveryResponseDTO;

@FeignClient(name = "delivery-api", url = "http://localhost:8085")
public interface DeliveryClient {
    @PostMapping("/api/delivery/generate-bill")
    DeliveryResponseDTO createDeliveryBill(@RequestBody DeliveryRequestPayload payload);
} 