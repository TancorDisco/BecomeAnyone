package ru.sweetbun.BecomeAnyone.config;


import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.sweetbun.BecomeAnyone.DTO.*;
import ru.sweetbun.BecomeAnyone.entity.Module;
import ru.sweetbun.BecomeAnyone.entity.*;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration()
                .setSkipNullEnabled(true)
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                .setMethodAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PUBLIC)
                .setFieldMatchingEnabled(true);

        Converter<UpdateModuleInCourseDTO, Module> updateModuleInCourseDTOModuleConverter = context -> {
            Module module = context.getDestination();
            skip(context.getSource(), module, "id", "lessons");
            return module;
        };
        modelMapper.addConverter(updateModuleInCourseDTOModuleConverter);

        Converter<UpdateLessonInCourseDTO, Lesson> updateLessonInCourseDTOLessonConverter = context -> {
            Lesson lesson = context.getDestination();
            skip(context.getSource(), lesson, "id");
            return lesson;
        };
        modelMapper.addConverter(updateLessonInCourseDTOLessonConverter);

        Converter<CreateModuleDTO, Module> createModuleDTOModuleConverter = context -> {
            Module module = context.getDestination();
            skip(context.getSource(), module, "lessons");
            return module;
        };
        modelMapper.addConverter(createModuleDTOModuleConverter);

        Converter<CourseDTO, Course> courseDTOCourseConverter = context -> {
            Course course = context.getDestination();
            skip(context.getSource(), course, "modules");
            return course;
        };
        modelMapper.addConverter(courseDTOCourseConverter);

        Converter<QuestionDTO, Question> questionDTOQuestionConverter = context -> {
            Question question = context.getDestination();
            skip(context.getSource(), question, "answers");
            return question;
        };
        modelMapper.addConverter(questionDTOQuestionConverter);

        Converter<Test, Test> testTestConverter = context -> {
            Test testToSend = context.getDestination();
            skip(context.getSource(), testToSend, "questions");
            return testToSend;
        };
        modelMapper.addConverter(testTestConverter);

        return modelMapper;
    }

    private static <T> void skip(T source, T target, String... fieldsToSkip) {
        Set<String> fieldsToSkipSet = new HashSet<>(Arrays.asList(fieldsToSkip));

        for (Field field : source.getClass().getDeclaredFields()) {
            if (fieldsToSkipSet.contains(field.getName())) {
                continue;
            }
            try {
                field.setAccessible(true);
                Object value = field.get(source);
                field.set(target, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Error copying fields", e);
            }
        }
    }
}
