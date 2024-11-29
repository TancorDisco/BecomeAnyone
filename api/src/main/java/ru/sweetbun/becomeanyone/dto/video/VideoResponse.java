package ru.sweetbun.becomeanyone.dto.video;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoResponse {

    private Long id;
    private String videoId;
    private String platform;
    private String accessKey;
}
