package ru.sweetbun.becomeanyone.dto.message;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileDeletionMessage implements Serializable {
    private String fileKey;
}
