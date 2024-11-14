package ru.sweetbun.becomeanyone.dto;

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
