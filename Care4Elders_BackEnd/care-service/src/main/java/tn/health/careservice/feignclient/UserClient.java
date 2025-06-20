package tn.health.careservice.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import tn.health.careservice.dto.UserDTO;

@FeignClient(name = "user-service", path = "/api/utilisateur", fallback = UserClientFallback.class)
public interface UserClient {
    @GetMapping("/afficherUtilisateurParId/{id}")
    UserDTO getUserById(@PathVariable("id") String id);
}


