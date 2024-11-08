package ru.sweetbun.BecomeAnyone.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import ru.sweetbun.BecomeAnyone.DTO.UpdateLessonInCourseDTO;
import ru.sweetbun.BecomeAnyone.config.ModelMapperConfig;
import ru.sweetbun.BecomeAnyone.entity.Lesson;
import ru.sweetbun.BecomeAnyone.entity.Module;
import ru.sweetbun.BecomeAnyone.mapper.UpdateLessonInCourseMapper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LessonServiceTests {

    private Module module;
    private Map<Long, Lesson> currentLessonsMap;
    private final UpdateLessonInCourseMapper updateLessonInCourseMapper = UpdateLessonInCourseMapper.INSTANCE;;
    private ModelMapper modelMapper;

    @BeforeEach
    public void setUp() {
        module = new Module();
        currentLessonsMap = new HashMap<>();
        modelMapper = ModelMapperConfig.createConfiguredModelMapper();

        Lesson lesson1 = Lesson.builder().id(1L).title("Lesson 1").orderNum(1).module(module).build();
        Lesson lesson2 = Lesson.builder().id(2L).title("Lesson 2").orderNum(2).module(module).build();
        Lesson lesson3 = Lesson.builder().id(3L).title("Lesson 3").orderNum(3).module(module).build();
        currentLessonsMap.put(1L, lesson1);
        currentLessonsMap.put(2L, lesson2);
        currentLessonsMap.put(3L, lesson3);
    }

    @Test
    public void mergeLessons_ExistingLessonIds_ShouldUpdateLessons() {
        // Arrange
        List<UpdateLessonInCourseDTO> lessonDTOS = Arrays.asList(
                UpdateLessonInCourseDTO.builder().id(1L).title("Updated Lesson 1").build(),
                UpdateLessonInCourseDTO.builder().id(2L).build()
        );

        // Act
        List<Lesson> updatedLessons = LessonService.mergeLessons(lessonDTOS, modelMapper, currentLessonsMap, module);

        // Assert
        assertEquals(2, updatedLessons.size());
        assertEquals("Updated Lesson 1", updatedLessons.get(0).getTitle());
        assertEquals("Lesson 2", updatedLessons.get(1).getTitle());
        assertEquals(1, currentLessonsMap.size());
    }
}