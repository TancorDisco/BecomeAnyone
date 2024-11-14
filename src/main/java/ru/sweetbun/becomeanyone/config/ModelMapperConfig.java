package ru.sweetbun.becomeanyone.config;


import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.sweetbun.becomeanyone.dto.*;
import ru.sweetbun.becomeanyone.entity.Module;
import ru.sweetbun.becomeanyone.entity.*;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        return createConfiguredModelMapper();
    }

    public static ModelMapper createConfiguredModelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration()
                .setSkipNullEnabled(true)
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                .setMethodAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PUBLIC)
                .setFieldMatchingEnabled(true);

        modelMapper.createTypeMap(UpdateModuleInCourseDTO.class, Module.class)
                .addMappings(mapper -> {
                    mapper.skip(Module::setId);
                    mapper.skip(Module::setLessons);
                });

        modelMapper.createTypeMap(UpdateLessonInCourseDTO.class, Lesson.class)
                .addMappings(mapper -> mapper.skip(Lesson::setId));

        modelMapper.createTypeMap(CreateModuleDTO.class, Module.class)
                .addMappings(mapper -> mapper.skip(Module::setLessons));

        modelMapper.createTypeMap(CourseDTO.class, Course.class)
                .addMappings(mapper -> mapper.skip(Course::setModules));

        modelMapper.createTypeMap(QuestionDTO.class, Question.class)
                .addMappings(mapper -> mapper.skip(Question::setAnswers));

        modelMapper.createTypeMap(Test.class, Test.class)
                .addMappings(mapper -> mapper.skip(Test::setQuestions));

        modelMapper.createTypeMap(ContentDTO.class, Content.class)
                .addMappings(mapper -> mapper.skip(Content::setVideo));

        return modelMapper;
    }
}
