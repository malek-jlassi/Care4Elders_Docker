package tn.health.careservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CareServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CareServiceApplication.class, args);
	}

}
