package ru.sweetbun.becomeanyone.contract;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {

    String uploadFile(MultipartFile multipartFile, Long lessonId) throws IOException;

    String getDownloadUrl(Long id);
}
