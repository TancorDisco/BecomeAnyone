package ru.sweetbun.becomeanyone.dto.content;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentDTO {

    private String text;
    private String videoUrl;
}
