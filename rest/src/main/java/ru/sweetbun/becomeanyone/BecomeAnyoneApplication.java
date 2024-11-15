package ru.sweetbun.becomeanyone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "ru.sweetbun.becomeanyone.client")
public class BecomeAnyoneApplication {

	public static void main(String[] args) {
		SpringApplication.run(BecomeAnyoneApplication.class, args);
	}
}
