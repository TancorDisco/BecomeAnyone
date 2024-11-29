package ru.sweetbun.becomeanyone.dto.course;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.*;
import ru.sweetbun.becomeanyone.dto.module.response.ModuleResponse;
import ru.sweetbun.becomeanyone.dto.user.response.UserResponse;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse {

    private Long id;
    private UserResponse teacher;
    private String title;
    private String description;
    private String requirements;
    private String coursePlan;
    @JsonProperty("createdAt")
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate createdAt;
    @JsonProperty("updatedAt")
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate updatedAt;
    private List<ModuleResponse> modules;
}
