package ru.sweetbun.becomeanyone.api.dto;

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
