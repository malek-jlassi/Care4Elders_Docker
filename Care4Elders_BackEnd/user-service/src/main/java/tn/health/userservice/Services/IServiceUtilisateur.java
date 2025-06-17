package tn.health.userservice.Services;

import tn.health.userservice.Entities.Utilisateur;

import java.util.List;

public interface IServiceUtilisateur {
    Utilisateur ajouterUtilisateur(Utilisateur utilisateur);
    Utilisateur mettreAJourUtilisateur(Utilisateur utilisateur);  // New method for updating a user
    Utilisateur mettreAJourUtilisateurId(String id, Utilisateur utilisateur);  // Mettre à jour un utilisateur par ID
    void supprimerUtilisateur(String id);  // New method for deleting a user by ID
    List<Utilisateur> afficherListeUtilisateurs();  // New method for displaying all users
    Utilisateur afficherUtilisateurParId(String id);
    Utilisateur inscriptionUtilisateur(Utilisateur utilisateur);  // Ajouter la méthode d'inscription
    boolean modifierMotDePasse(String id, String ancienMotDePasse, String nouveauMotDePasse);
    //void resetPassword(String email);

}
