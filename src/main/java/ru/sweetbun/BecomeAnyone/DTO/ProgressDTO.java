package ru.sweetbun.BecomeAnyone.DTO;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sweetbun.BecomeAnyone.entity.Enrollment;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ProgressDTO {

    private int completedLessons;
    private List<Integer> testResults;
}
