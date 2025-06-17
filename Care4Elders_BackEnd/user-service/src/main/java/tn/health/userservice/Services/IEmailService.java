package tn.health.userservice.Services;

public interface IEmailService {
     void resetPassword(String email);
    void updatePasswordWithToken(String token, String nouveauMotDePasse);
    void sendResetPasswordEmail(String to,String token);
}
