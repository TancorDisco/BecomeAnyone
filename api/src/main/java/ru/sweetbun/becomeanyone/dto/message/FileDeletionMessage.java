package ru.sweetbun.becomeanyone.dto.message;

import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileDeletionMessage implements Serializable {
    private String fileKey;
}
