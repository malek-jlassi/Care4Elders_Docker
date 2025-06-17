package tn.health.telemedservice.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Service
public class WherebyService {

    private static final String API_URL = "https://api.whereby.dev/v1/meetings";
    @Value("${whereby.api.key}")
    private String apiKey;

    public String createWherebyMeeting(LocalDateTime start, int durationMinutes) {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            LocalDateTime end = start.plusMinutes(durationMinutes);

            // Construction du JSON à envoyer
            String jsonInput = String.format(
                    "{\"startDate\": \"%sZ\", \"endDate\": \"%sZ\", \"roomMode\": \"group\"}",
                    start.toString(), end.toString()
            );

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInput.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int code = conn.getResponseCode();
            if (code != 200 && code != 201) {
                throw new RuntimeException("Échec de création de la salle Whereby. Code : " + code);
            }

            // Lire la réponse
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {

                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                // Extraire le lien Whereby
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.toString());
                return root.get("roomUrl").asText(); // C'est le lien que tu mets dans videoLink
            }

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'appel à l'API Whereby", e);
        }
    }
}
