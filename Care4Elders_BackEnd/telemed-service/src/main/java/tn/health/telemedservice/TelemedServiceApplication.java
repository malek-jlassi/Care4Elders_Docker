package tn.health.telemedservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class TelemedServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TelemedServiceApplication.class, args);
	}

}
