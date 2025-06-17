package tn.health.userservice.Entities;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;

@Document(collection = "utilisateurs")
@Getter
@Setter
@Data
@NoArgsConstructor // Lombok génère le constructeur sans arguments
@AllArgsConstructor // Lombok génère le constructeur avec tous les arguments
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)

public class Utilisateur implements UserDetails{
    @Id
    private String id;  // L'ID sera généré automatiquement par MongoDB


    private String username;// username d'utilisateur
    private String email;// Email de l'utilisateur
    String phoneNumber;         // <-- Numéro de téléphone
    LocalDate dateOfBirth;
    private String password;  // Mot de passe de l'

    //Attributs Patient
    private String adressePatient;
    private String dossierMedicalPdf; // Nom ou URL du fichier

   //Attributs Medcin
   private String specialite;
    private String numeroOrdre; // Numéro d’enregistrement médical
    private String fichierDiplome;// Lien ou nom du fichier PDF

    //Attributs Aidant
    private String adresseAidant;
    private String cartedIentite;


    @Enumerated(EnumType.STRING)
    private Role role;      // Rôle de l'utilisateur (ADMIN, PATIENT, AIDANT, MEDECIN)



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(role);
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


}
