package ru.sweetbun.becomeanyone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.sweetbun.becomeanyone.contract.FileService;
import ru.sweetbun.becomeanyone.feign.FileServiceClient;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileServiceClient fileServiceClient;

    @Override
    public String uploadFile(MultipartFile multipartFile, Long lessonId) throws IOException {
        return fileServiceClient.uploadFile(multipartFile, lessonId);
    }

    @Override
    public String getDownloadUrl(Long id) {
        return fileServiceClient.getDownloadUrl(id);
    }

    @Override
    public Long deleteFIle(Long id) {
        return fileServiceClient.deleteFIle(id);
    }
}
