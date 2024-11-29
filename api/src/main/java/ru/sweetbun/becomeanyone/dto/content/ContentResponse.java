package ru.sweetbun.becomeanyone.dto.content;

import lombok.*;
import ru.sweetbun.becomeanyone.dto.file.FileResponse;
import ru.sweetbun.becomeanyone.dto.video.VideoResponse;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentResponse {

    private Long id;
    private String text;
    private VideoResponse video;
    private List<FileResponse> files;
}
