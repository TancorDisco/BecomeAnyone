package ru.sweetbun.becomeanyone.dto.module.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import ru.sweetbun.becomeanyone.dto.lesson.request.CreateLessonRequest;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateModuleRequest implements ModuleRequest {
    private String title;
    private int orderNum;
    @Builder.Default
    private List<CreateLessonRequest> lessons = new ArrayList<>();
}