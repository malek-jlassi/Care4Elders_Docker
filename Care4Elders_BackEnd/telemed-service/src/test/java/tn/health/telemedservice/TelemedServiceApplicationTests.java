package tn.health.telemedservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import com.google.api.services.calendar.Calendar;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class TelemedServiceApplicationTests {

	@MockBean
	private Calendar calendarService;

	@Test
	void contextLoads() {
	}

}
