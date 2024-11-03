package ru.sweetbun.BecomeAnyone.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.sweetbun.BecomeAnyone.DTO.*;
import ru.sweetbun.BecomeAnyone.entity.Course;
import ru.sweetbun.BecomeAnyone.entity.Lesson;
import ru.sweetbun.BecomeAnyone.entity.Module;
import ru.sweetbun.BecomeAnyone.entity.Question;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setSkipNullEnabled(true)
                .setMatchingStrategy(MatchingStrategies.STRICT);

        modelMapper.createTypeMap(UpdateModuleInCourseDTO.class, ru.sweetbun.BecomeAnyone.entity.Module.class)
                .addMappings(mapper -> {
                    mapper.skip(ru.sweetbun.BecomeAnyone.entity.Module::setId);
                    mapper.skip(ru.sweetbun.BecomeAnyone.entity.Module::setLessons);
                });

        modelMapper.createTypeMap(UpdateLessonInCourseDTO.class, Lesson.class)
                .addMappings(mapper -> mapper.skip(Lesson::setId));

        modelMapper.createTypeMap(CreateModuleDTO.class, ru.sweetbun.BecomeAnyone.entity.Module.class)
                .addMappings(mapper -> mapper.skip(Module::setLessons));

        modelMapper.createTypeMap(CourseDTO.class, Course.class)
                .addMappings(mapper -> mapper.skip(Course::setModules));

        modelMapper.createTypeMap(QuestionDTO.class, Question.class)
                .addMappings(mapper -> mapper.skip(Question::setAnswers));

        return modelMapper;
    }
}
