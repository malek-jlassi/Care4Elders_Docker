package tn.health.userservice.RestControllers;

import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.health.userservice.Entities.Role;
import tn.health.userservice.Entities.Utilisateur;
import tn.health.userservice.Services.IServiceUtilisateur;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

//@CrossOrigin(origins = "http://localhost:4200")
@RestController
@AllArgsConstructor
@RequestMapping("/api/utilisateur")
public class RestControllerUtilisateur {

    IServiceUtilisateur serviceUtilisateur;

    @PostMapping("ajouterUtilisateur")
    public Utilisateur ajouterArbitre(@RequestBody Utilisateur utilisateur) {
        return serviceUtilisateur.ajouterUtilisateur(utilisateur);
    }

   /* // Mettre à jour un utilisateur
    @PutMapping("/mettreAJourUtilisateur")
    public Utilisateur mettreAJourUtilisateur(@RequestBody Utilisateur utilisateur) {
        return serviceUtilisateur.mettreAJourUtilisateur(utilisateur);
    }*/
   @PutMapping("/mettreAJourUtilisateur")
   public ResponseEntity<?> mettreAJourUtilisateur(
           @RequestParam("id") String id,
           @RequestParam("username") String username,
           @RequestParam("email") String email,
           @RequestParam("phoneNumber") String phoneNumber,
           @RequestParam("dateOfBirth") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateOfBirth,
           @RequestParam("role") String roleStr,

           // Champs spécifiques
           @RequestParam(value = "adressePatient", required = false) String adressePatient,
           @RequestParam(value = "dossierMedicalPdf", required = false) MultipartFile dossierMedicalPdf,
           @RequestParam(value = "specialite", required = false) String specialite,
           @RequestParam(value = "numeroOrdre", required = false) String numeroOrdre,
           @RequestParam(value = "fichierDiplome", required = false) MultipartFile fichierDiplome,
           @RequestParam(value = "adresseAidant", required = false) String adresseAidant,
           @RequestParam(value = "cartedIentite", required = false) MultipartFile cartedIentite
   ) throws IOException {
       Utilisateur utilisateur = serviceUtilisateur.afficherUtilisateurParId(id);

       if (utilisateur == null) {
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur non trouvé");
       }

       // Mettre à jour les champs
       utilisateur.setUsername(username);
       utilisateur.setEmail(email);
       utilisateur.setPhoneNumber(phoneNumber);
       utilisateur.setDateOfBirth(dateOfBirth);
       utilisateur.setRole(Role.valueOf(roleStr.toUpperCase()));

       // Champs Patient
       utilisateur.setAdressePatient(adressePatient);
       if (dossierMedicalPdf != null && !dossierMedicalPdf.isEmpty()) {
           String dossierFileName = UUID.randomUUID() + "_" + dossierMedicalPdf.getOriginalFilename();
           Path uploadPath = Paths.get("uploads/dossiers");
           if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
           Path filePath = uploadPath.resolve(dossierFileName);
           Files.copy(dossierMedicalPdf.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
           utilisateur.setDossierMedicalPdf("/uploads/dossiers/" + dossierFileName);
       }

       // Champs Médecin
       utilisateur.setSpecialite(specialite);
       utilisateur.setNumeroOrdre(numeroOrdre);
       if (fichierDiplome != null && !fichierDiplome.isEmpty()) {
           String fileName = UUID.randomUUID() + "_" + fichierDiplome.getOriginalFilename();
           Path uploadPath = Paths.get("uploads/diplomes");
           if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
           Path filePath = uploadPath.resolve(fileName);
           Files.copy(fichierDiplome.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
           utilisateur.setFichierDiplome("/uploads/diplomes/" + fileName);
       }

       // Champs Aidant
       utilisateur.setAdresseAidant(adresseAidant);
       if (cartedIentite != null && !cartedIentite.isEmpty()) {
           String fileName = UUID.randomUUID() + "_" + cartedIentite.getOriginalFilename();
           Path uploadPath = Paths.get("uploads/identites");
           if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
           Path filePath = uploadPath.resolve(fileName);
           Files.copy(cartedIentite.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
           utilisateur.setCartedIentite("/uploads/identites/" + fileName);
       }

       Utilisateur updatedUser = serviceUtilisateur.mettreAJourUtilisateur(utilisateur);

       return ResponseEntity.ok(updatedUser);
   }

    // Mettre à jour un utilisateur par son ID
    @PutMapping("/mettreAJourUtilisateurParId/{id}")
    public Utilisateur mettreAJourUtilisateur(@PathVariable String id, @RequestBody Utilisateur utilisateur) {
        return serviceUtilisateur.mettreAJourUtilisateurId(id, utilisateur);  // Passer l'ID et les informations mises à jour
    }
    // Supprimer un utilisateur
    @DeleteMapping("/supprimerUtilisateur/{id}")
    public void supprimerUtilisateur(@PathVariable String id) {
        serviceUtilisateur.supprimerUtilisateur(id);
    }


    // Afficher la liste de tous les utilisateurs
    @GetMapping("/afficherListeUtilisateurs")
    public List<Utilisateur> afficherListeUtilisateurs() {
        return serviceUtilisateur.afficherListeUtilisateurs();
    }


    @GetMapping("/afficherUtilisateurParId/{id}")
    public ResponseEntity<?> afficherUtilisateurParId(@PathVariable String id) {
        try {
            Utilisateur utilisateur = serviceUtilisateur.afficherUtilisateurParId(id);
            return ResponseEntity.ok(utilisateur);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    /*@PostMapping("/inscription")
    public ResponseEntity<?> inscrireUtilisateur(@RequestBody Utilisateur utilisateur) {
        try {
            System.out.println("📥 Tentative d'inscription pour l'utilisateur : " + utilisateur.getEmail());

            Utilisateur savedUser = serviceUtilisateur.inscriptionUtilisateur(utilisateur);

            System.out.println("✅ Inscription réussie pour : " + savedUser.getEmail());
            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            System.out.println("❌ Échec de l'inscription : " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }*/
    @PostMapping("/inscription")
    public ResponseEntity<?> inscrireUtilisateur(
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("dateOfBirth") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateOfBirth,
            @RequestParam("role") String roleStr,

            // Champs spécifiques
            @RequestParam(value = "adressePatient", required = false) String adressePatient,
            @RequestParam(value = "dossierMedicalPdf", required = false) MultipartFile dossierMedicalPdf,
            @RequestParam(value = "specialite", required = false) String specialite,
            @RequestParam(value = "numeroOrdre", required = false) String numeroOrdre,
            @RequestParam(value = "fichierDiplome", required = false) MultipartFile fichierDiplome,
            @RequestParam(value = "adresseAidant", required = false) String adresseAidant,
            @RequestParam(value = "cartedIentite", required = false) MultipartFile cartedIentite
    ) throws IOException {

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setUsername(username);
        utilisateur.setEmail(email);
        utilisateur.setPassword(password);
        utilisateur.setPhoneNumber(phoneNumber);
        utilisateur.setDateOfBirth(dateOfBirth);
        utilisateur.setRole(Role.valueOf(roleStr.toUpperCase()));

        // Champs Patient
        utilisateur.setAdressePatient(adressePatient);
        if (dossierMedicalPdf != null && !dossierMedicalPdf.isEmpty()) {
            String dossierFileName = UUID.randomUUID() + "_" + dossierMedicalPdf.getOriginalFilename();
            Path uploadPath = Paths.get("uploads/dossiers");
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
            Path filePath = uploadPath.resolve(dossierFileName);
            Files.copy(dossierMedicalPdf.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            utilisateur.setDossierMedicalPdf("/uploads/dossiers/" + dossierFileName);
        }

        // Champs Médecin
        utilisateur.setSpecialite(specialite);
        utilisateur.setNumeroOrdre(numeroOrdre);
        if (fichierDiplome != null && !fichierDiplome.isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + fichierDiplome.getOriginalFilename();
            Path uploadPath = Paths.get("uploads/diplomes");
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(fichierDiplome.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            utilisateur.setFichierDiplome("/uploads/diplomes/" + fileName);
        }

        // Champs Aidant
        utilisateur.setAdresseAidant(adresseAidant);
        if (cartedIentite != null && !cartedIentite.isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + cartedIentite.getOriginalFilename();
            Path uploadPath = Paths.get("uploads/identites");
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(cartedIentite.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            utilisateur.setCartedIentite("/uploads/identites/" + fileName);
        }

        try {
            System.out.println("📥 Tentative d'inscription pour l'utilisateur : " + utilisateur.getEmail());

            Utilisateur savedUser = serviceUtilisateur.inscriptionUtilisateur(utilisateur);

            System.out.println("✅ Inscription réussie pour : " + savedUser.getEmail());
            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            System.out.println("❌ Échec de l'inscription : " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        }


    @PutMapping("/modifierMotDePasse/{id}")
    public ResponseEntity<String> modifierMotDePasse(
            @PathVariable String id,
            @RequestBody Map<String, String> passwords) {

        String ancienMotDePasse = passwords.get("ancienMotDePasse");
        String nouveauMotDePasse = passwords.get("nouveauMotDePasse");

        boolean isUpdated = serviceUtilisateur.modifierMotDePasse(id, ancienMotDePasse, nouveauMotDePasse);

        if (isUpdated) {
            return ResponseEntity.ok("Mot de passe mis à jour avec succès.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Échec de la mise à jour du mot de passe.");
        }
    }



}
