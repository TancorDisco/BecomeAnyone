package ru.sweetbun.becomeanyone.dto.file;

import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileResponse {
    private Long id;
    private String originalFileName;
    private String contentType;
    private Long size;
}
