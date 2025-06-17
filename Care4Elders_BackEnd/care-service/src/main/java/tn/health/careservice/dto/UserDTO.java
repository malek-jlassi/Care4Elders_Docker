package tn.health.careservice.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserDTO (
        String id,
        String username,
       // String lastName,
        String adressePatient,
        int phoneNumber,
        String email
       // String role    // "PATIENT"; "DOCTOR"; "ADMIN"; etc.

//         String id,
//        String firstName,
//        String lastName,
//        String address,
//        int phoneNumber,
//        String email
//        // String role    // "PATIENT"; "DOCTOR"; "ADMIN"; etc.
)
{
}

//private String ;// username d'utilisateur
//private String email;// Email de l'utilisateur
//String phoneNumber;         // <-- Numéro de téléphone
//LocalDate dateOfBirth;
//private String password;  // Mot de passe de l'
//
////Attributs Patient
//private String ;
//private String dossierMedicalPdf;
