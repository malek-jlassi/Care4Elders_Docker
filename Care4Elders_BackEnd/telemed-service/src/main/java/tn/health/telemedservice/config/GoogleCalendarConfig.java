package tn.health.telemedservice.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.util.Collections;

@Configuration
public class GoogleCalendarConfig {

    private static final String APPLICATION_NAME = "Care4Elders";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    @Bean
    public Calendar calendarService() {
        try {
            // ✅ Lire le fichier de credentials depuis src/main/resources
            ClassPathResource resource = new ClassPathResource("service-account.json");
            InputStream credentialsStream = resource.getInputStream();

            // ✅ Créer l’objet Credential avec la portée appropriée
            GoogleCredential credential = GoogleCredential.fromStream(credentialsStream)
                    .createScoped(Collections.singleton(CalendarScopes.CALENDAR));

            // ✅ Construire le client Google Calendar
            return new Calendar.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JSON_FACTORY,
                    credential
            ).setApplicationName(APPLICATION_NAME).build();

        } catch (Exception e) {
            throw new RuntimeException("❌ Erreur lors de l'initialisation du client Google Calendar", e);
        }
    }
}