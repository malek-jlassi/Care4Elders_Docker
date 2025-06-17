package tn.health.telemedservice.services;


import org.springframework.stereotype.Service;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.client.util.DateTime;



@Service
public class GoogleCalendarService {

    private final Calendar calendarService;

    public GoogleCalendarService(Calendar calendarService) {
        this.calendarService = calendarService;
    }

    /**

     Crée un événement dans Google Calendar en utilisant un lien Whereby existant*
     @param wherebyLink le lien Whereby à insérer dans l'événement*/
    public void createGoogleCalendarEvent(String wherebyLink, LocalDateTime startTime, int durationMinutes) throws IOException {
        ZonedDateTime startZoned = startTime.atZone(ZoneId.of("UTC"));
        ZonedDateTime endZoned = startZoned.plusMinutes(durationMinutes);

        Event event = new Event()
                .setSummary("Téléconsultation")
                .setLocation(wherebyLink)
                .setDescription("Lien : " + wherebyLink);

        event.setStart(new EventDateTime().setDateTime(new DateTime(startZoned.toInstant().toString())).setTimeZone("UTC"));
        event.setEnd(new EventDateTime().setDateTime(new DateTime(endZoned.toInstant().toString())).setTimeZone("UTC"));

        calendarService.events().insert("primary", event).execute();
    }
}
