package tn.health.careservice.services;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j


public class DoctorSuggestionService implements IDoctorSuggestionService {



    private final RestTemplate restTemplate = new RestTemplate();


    @Override
    public String suggestDoctorSpecialty(String description) {
        String url = "http://ai-service:11434/api/generate";

        String prompt = "Given the following care request description: \"" + description +
                "\", what kind of doctor specialty or healthcare worker is most appropriate to handle this case? " +
                "Please answer with only the specialty name (like General Practitioner, Geriatrician, Nurse, etc.).";

        Map<String, Object> body = new HashMap<>();
        body.put("model", "mistral");
        body.put("prompt", prompt);
        body.put("stream", false);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            return (String) response.getBody().get("response");
        } catch (Exception e) {
            log.error("AI model error: {}", e.getMessage());
            return "General Practitioner"; // default fallback
        }
    }
}
