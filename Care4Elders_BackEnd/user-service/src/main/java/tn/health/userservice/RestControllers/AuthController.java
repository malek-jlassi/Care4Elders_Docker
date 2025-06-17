package tn.health.userservice.RestControllers;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.health.userservice.Entities.Utilisateur;
import tn.health.userservice.Services.AuthenticationService;

import java.util.Map;
//@CrossOrigin(origins = "http://localhost:4200")
@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        try {
            String token = authenticationService.login(email, password);
            Utilisateur utilisateur = authenticationService.getUserByEmail(email); // Récupérez l'utilisateur

            System.out.println("✅ Connexion réussie pour l'utilisateur : " + email);

            return ResponseEntity.ok(Map.of(
                    "message", "Authentification réussie",
                    "token", token,
                    "id", utilisateur != null ? utilisateur.getId() : null // Assurez-vous que l'utilisateur n'est pas null
            ));
        } catch (RuntimeException e) {
            System.out.println("❌ Échec de connexion pour l'utilisateur : " + email + " - Raison : " + e.getMessage());

            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Échec de l'authentification : " + e.getMessage()
            ));
        }
    }




    @Setter
    @Getter
    public static class AuthResponse {
        private String token;

        public AuthResponse(String token) {
            this.token = token;
        }

    }
}
