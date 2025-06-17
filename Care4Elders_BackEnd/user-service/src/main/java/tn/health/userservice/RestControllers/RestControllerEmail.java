package tn.health.userservice.RestControllers;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.health.userservice.DTO.PasswordResetRequest;
import tn.health.userservice.Services.EmailService;

import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/Email")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:4300"})
public class RestControllerEmail {

    private final EmailService emailService;

    // Endpoint pour la réinitialisation de mot de passe via email
    @PostMapping("/resetPasswordEmail")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        try {
            emailService.resetPassword(email);
            return ResponseEntity.ok("Un email de réinitialisation de mot de passe a été envoyé.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erreur : " + e.getMessage());
        }
    }

    @PostMapping("/updatePassword")
    public ResponseEntity<String> updatePassword(@RequestBody PasswordResetRequest request) {
        try {
            emailService.updatePasswordWithToken(request.getToken(), request.getNouveauMotDePasse());
            return ResponseEntity.ok("Mot de passe mis à jour avec succès !");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erreur : " + e.getMessage());
        }
    }

}
