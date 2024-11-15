package ru.sweetbun.becomeanyone.config;


import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.sweetbun.becomeanyone.domain.entity.*;
import ru.sweetbun.becomeanyone.domain.entity.Module;
import ru.sweetbun.becomeanyone.dto.*;
import ru.sweetbun.becomeanyone.dto.course.CourseRequest;
import ru.sweetbun.becomeanyone.dto.lesson.request.UpdateLessonInCourseRequest;
import ru.sweetbun.becomeanyone.dto.module.request.CreateModuleRequest;
import ru.sweetbun.becomeanyone.dto.module.request.UpdateModuleInCourseRequest;

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

        modelMapper.createTypeMap(UpdateModuleInCourseRequest.class, Module.class)
                .addMappings(mapper -> {
                    mapper.skip(Module::setId);
                    mapper.skip(Module::setLessons);
                });

        modelMapper.createTypeMap(UpdateLessonInCourseRequest.class, Lesson.class)
                .addMappings(mapper -> mapper.skip(Lesson::setId));

        modelMapper.createTypeMap(CreateModuleRequest.class, Module.class)
                .addMappings(mapper -> mapper.skip(Module::setLessons));

        modelMapper.createTypeMap(CourseRequest.class, Course.class)
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
