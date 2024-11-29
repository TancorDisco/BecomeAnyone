package ru.sweetbun.becomeanyone.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@FeignClient(name = "fileService", url = "http:/localhost:8080")
public interface FileServiceClient {

    @PostMapping("/courses/{courseId}/modules/{moduleId}/lessons/{lessonId}/files")
    String uploadFile(@RequestParam("file") MultipartFile multipartFile,
                      @PathVariable("lessonId") Long lessonId) throws IOException;

    @GetMapping("/courses/{courseId}/modules/{moduleId}/lessons/{lessonId}/files/{id}/download-url")
    String getDownloadUrl(@PathVariable Long id);

    @DeleteMapping("/courses/{courseId}/modules/{moduleId}/lessons/{lessonId}/files/{id}")
    Long deleteFIle(@PathVariable Long id);
}
