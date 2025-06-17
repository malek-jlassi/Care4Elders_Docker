package tn.health.userservice.Repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import tn.health.userservice.Entities.PasswordResetToken;

import java.util.Optional;

public interface PasswordResetTokenRepository extends MongoRepository<PasswordResetToken, String> {
    Optional<PasswordResetToken> findByToken(String token);
    Optional<PasswordResetToken> findByEmail(String email);
    // Méthode pour supprimer les tokens par email
    void deleteByEmail(String email);
    void deleteByToken(String token);
    void getUserByEmail(String email);

}
