package ru.sweetbun.becomeanyone.dto.rabbitmq;

import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileDeletionMessage implements Serializable {
    private String fileKey;
}
