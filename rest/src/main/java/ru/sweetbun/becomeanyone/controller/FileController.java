package ru.sweetbun.becomeanyone.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.sweetbun.becomeanyone.aop.CheckCourseOwner;
import ru.sweetbun.becomeanyone.contract.FileService;

import java.io.IOException;

import static org.springframework.http.ResponseEntity.ok;

@Tag(name = "File Management", description = "API для управления файлами")
@RequiredArgsConstructor
@RestController
@RequestMapping("/courses/{courseId}/modules/{moduleId}/lessons/{lessonId}/files")
public class FileController {

    private final FileService fileService;

    @PreAuthorize("hasRole('TEACHER')")
    @CheckCourseOwner
    @Operation(summary = "Добавить файл", description = "Прикрепление файла к уроку")
    @PostMapping
    public ResponseEntity<?> uploadFile(@PathVariable Long lessonId,
                                        @RequestParam("file") MultipartFile file) throws IOException {
        return ok(fileService.uploadFile(file, lessonId));
    }

    @GetMapping("{id}/download-url")
    public ResponseEntity<?> getDownloadUrl(@PathVariable Long id) {
        return ok(fileService.getDownloadUrl(id));
    }
}
