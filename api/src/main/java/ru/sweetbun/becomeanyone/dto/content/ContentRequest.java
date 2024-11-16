package ru.sweetbun.becomeanyone.dto.content;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentRequest {

    private String text;
    private String videoUrl;
}
