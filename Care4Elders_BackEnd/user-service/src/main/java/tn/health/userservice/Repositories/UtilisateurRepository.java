package tn.health.userservice.Repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import tn.health.userservice.Entities.Role;
import tn.health.userservice.Entities.Utilisateur;

import java.util.List;
import java.util.Optional;

public interface UtilisateurRepository extends MongoRepository<Utilisateur, String> {
    Optional<Utilisateur> findByEmail(String email); // Vérifier si un email existe déjà
    Optional<Utilisateur> findByUsername(String username); // Vérifier si un username existe déjà

    List<Utilisateur> findByRole(Role role);

}
