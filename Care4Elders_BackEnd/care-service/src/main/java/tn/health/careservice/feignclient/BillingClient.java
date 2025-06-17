package tn.health.careservice.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import tn.health.careservice.dto.CareRequestPayload;
import tn.health.careservice.dto.CareServiceRequestDTO;
import tn.health.careservice.dto.FactureResponseDTO;

@FeignClient(name = "billing-api", url = "http://localhost:8084") // Replace with actual billing port
public interface BillingClient {
    @PostMapping("/factures/generate")
    FactureResponseDTO createBillFromRequest(@RequestBody CareRequestPayload payload);
}

