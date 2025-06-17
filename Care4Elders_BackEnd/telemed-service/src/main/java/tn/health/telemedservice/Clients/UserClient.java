package tn.health.telemedservice.Clients;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import tn.health.telemedservice.DTO.UserDTO;

import java.util.List;

@Primary
@FeignClient(name = "user-service", path = "/api/utilisateur", fallback = UserClientFallback.class)
public interface UserClient {

    @GetMapping("/afficherListeUtilisateurs")
    List<UserDTO> getAllUsers();
    @GetMapping("/users-by-role")
    List<UserDTO> getUserByRole(@RequestParam String role);
    @GetMapping("/afficherUtilisateurParId/{id}")
    UserDTO getUserById(@PathVariable String id);

}
