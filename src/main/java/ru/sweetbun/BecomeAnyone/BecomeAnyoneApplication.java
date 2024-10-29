package ru.sweetbun.BecomeAnyone;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ru.sweetbun.BecomeAnyone.DTO.UpdateLessonDTO;
import ru.sweetbun.BecomeAnyone.DTO.UpdateModuleDTO;
import ru.sweetbun.BecomeAnyone.entity.Lesson;
import ru.sweetbun.BecomeAnyone.entity.Module;

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

		modelMapper.createTypeMap(UpdateModuleDTO.class, Module.class)
				.addMappings(mapper -> mapper.skip(Module::setId));
		modelMapper.createTypeMap(UpdateLessonDTO.class, Lesson.class)
				.addMappings(mapper -> mapper.skip(Lesson::setId));

		return modelMapper;
	}
}
