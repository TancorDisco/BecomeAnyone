package ru.sweetbun.BecomeAnyone.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.sweetbun.BecomeAnyone.DTO.UpdateLessonInCourseDTO;
import ru.sweetbun.BecomeAnyone.entity.Lesson;

@Mapper
public interface UpdateLessonInCourseMapper {

    UpdateLessonInCourseMapper INSTANCE = Mappers.getMapper(UpdateLessonInCourseMapper.class);

    @Mapping(target = "id", ignore = true)
    Lesson toLesson(UpdateLessonInCourseDTO lessonDTO);
}
