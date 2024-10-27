package ru.sweetbun.BecomeAnyone;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BecomeAnyoneApplication {

	public static void main(String[] args) {
		SpringApplication.run(BecomeAnyoneApplication.class, args);
	}

	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration()
				.setSkipNullEnabled(true)
				.setMatchingStrategy(MatchingStrategies.STRICT);
		return modelMapper;
	}
}
