package tn.health.userservice.Services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tn.health.userservice.Entities.Role;
import tn.health.userservice.Entities.Utilisateur;
import tn.health.userservice.Repositories.UtilisateurRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor

public class ServiceUtilisateur implements IServiceUtilisateur{

    UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public Utilisateur inscriptionUtilisateur(Utilisateur utilisateur) {
        // Vérifier si l'email ou le username existe déjà
        if (utilisateurRepository.findByEmail(utilisateur.getEmail()).isPresent() ||
                utilisateurRepository.findByUsername(utilisateur.getUsername()).isPresent()) {
            throw new RuntimeException("L'email ou le username est déjà utilisé !");
        }

        // Hasher le mot de passe avant d'enregistrer
        utilisateur.setPassword(passwordEncoder.encode(utilisateur.getPassword()));

        return utilisateurRepository.save(utilisateur);
    }

    @Override
    public boolean modifierMotDePasse(String id, String ancienMotDePasse, String nouveauMotDePasse) {
        // 🔹 Récupérer l'utilisateur existant
        Utilisateur utilisateur = utilisateurRepository.findById(id).orElse(null);
        if (utilisateur == null) {
            log.warn("Utilisateur avec l'ID {} non trouvé", id);
            return false;
        }

        // 🔹 Vérifier si l'ancien mot de passe est correct
        if (!passwordEncoder.matches(ancienMotDePasse, utilisateur.getPassword())) {
            log.warn("L'ancien mot de passe est incorrect pour l'utilisateur ID {}", id);
            return false;
        }

        // 🔹 Hasher le nouveau mot de passe
        String hashedPassword = passwordEncoder.encode(nouveauMotDePasse);

        // 🔹 Mettre à jour Utilisateur
        utilisateur.setPassword(hashedPassword);
        utilisateurRepository.save(utilisateur);



        log.info("Mot de passe mis à jour avec succès pour l'utilisateur ID {}", id);
        return true;
    }


    @Override
    public Utilisateur ajouterUtilisateur(Utilisateur utilisateur) {
        // Delegate to inscriptionUtilisateur for consistent user creation (with password hashing and duplicate checks)
        return inscriptionUtilisateur(utilisateur);
    }

    @Override
    public Utilisateur mettreAJourUtilisateur(Utilisateur utilisateur) {
        // Vérifier si l'utilisateur existe
        if (utilisateurRepository.existsById(utilisateur.getId())) {
            return utilisateurRepository.save(utilisateur);  // Mettre à jour l'utilisateur existant
        } else {
            log.warn("Utilisateur avec l'ID {} n'existe pas", utilisateur.getId());
            return null;  // Retourner null si l'utilisateur n'existe pas
        }
    }

    @Override
    public Utilisateur mettreAJourUtilisateurId(String id, Utilisateur utilisateur) {
        // Récupérer l'utilisateur existant par ID
        Utilisateur utilisateurExistant = utilisateurRepository.findById(id).orElse(null);

        // Vérifier si l'utilisateur existe
        if (utilisateurExistant == null) {
            log.warn("Utilisateur avec l'ID {} n'existe pas", id);
            return null;  // L'utilisateur n'a pas été trouvé
        }


        if (utilisateur.getUsername() != null) {
            utilisateurExistant.setUsername(utilisateur.getUsername());
        }
        if (utilisateur.getEmail() != null) {
            utilisateurExistant.setEmail(utilisateur.getEmail());
        }
        if (utilisateur.getPassword() != null) {
            utilisateurExistant.setPassword(utilisateur.getPassword());
        }
        if (utilisateur.getRole() != null) {
            utilisateurExistant.setRole(utilisateur.getRole());
        }

        // Sauvegarder l'utilisateur mis à jour
        return utilisateurRepository.save(utilisateurExistant);
    }



    @Override
    public void supprimerUtilisateur(String id) {
        Optional<Utilisateur> utilisateur = utilisateurRepository.findById(id);
        if (utilisateur.isPresent()) {
            utilisateurRepository.delete(utilisateur.get());  // Supprimer l'utilisateur si trouvé
            log.info("Utilisateur avec l'ID {} supprimé", id);
        } else {
            log.warn("Utilisateur avec l'ID {} non trouvé", id);
        }
    }

    @Override
    public List<Utilisateur> afficherListeUtilisateurs() {
        return utilisateurRepository.findAll();  // Retourner tous les utilisateurs du repository
    }

    @Override
    public Utilisateur afficherUtilisateurParId(String id) {
        return utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur avec l'ID " + id + " non trouvé"));
    }

    public List<Utilisateur> getAllUsersByRole(Role role) {
        return utilisateurRepository.findByRole(role);
    }

}
