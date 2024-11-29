package ru.sweetbun.becomeanyone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "ru.sweetbun.becomeanyone")
public class BecomeAnyoneApplication {

	public static void main(String[] args) {
		SpringApplication.run(BecomeAnyoneApplication.class, args);
	}
}
