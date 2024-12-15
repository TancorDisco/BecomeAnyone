package ru.sweetbun.becomeanyone.dto.message;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage implements Serializable {

    private String to;
    private String subject;
    private String text;
}
