package ru.sweetbun.becomeanyone.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.sweetbun.becomeanyone.contract.FileService;
import ru.sweetbun.becomeanyone.entity.AttachmentFile;
import ru.sweetbun.becomeanyone.entity.Content;
import ru.sweetbun.becomeanyone.exception.ResourceNotFoundException;
import ru.sweetbun.becomeanyone.repository.FileRepository;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Transactional(readOnly = true)
@Service
public class FileServiceImpl implements FileService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final String bucketName;
    private final LessonServiceImpl lessonService;
    private final FileRepository fileRepository;
    private final Long MAX_FILE_SIZE;

    @Autowired
    public FileServiceImpl(S3Client s3Client, S3Presigner s3Presigner,
                           @Value("${vk-cloud.storage.bucket-name}") String bucketName, LessonServiceImpl lessonService,
                           FileRepository fileRepository,
                           @Value("${vk-cloud.storage.max-file-size}") Long maxFileSize) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
        this.bucketName = bucketName;
        this.lessonService = lessonService;
        this.fileRepository = fileRepository;
        this.MAX_FILE_SIZE = maxFileSize;
    }

    @Transactional
    @Override
    public String uploadFile(MultipartFile file, Long lessonId) throws IOException {
        validateFile(file);
        String originalFileName = file.getOriginalFilename();
        String key = UUID.randomUUID() + "_" + originalFileName;
        Path tempFile = Files.createTempFile("upload-", originalFileName);
        Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .build(),
                tempFile
        );
        Files.delete(tempFile);

        attachFileToContent(file, lessonId, key);
        return key;
    }

    private void validateFile(MultipartFile file) {
        if (file == null) throw new IllegalArgumentException("File is null");
        checkFileExtension(Objects.requireNonNull(file.getOriginalFilename()));
        checkFileSize(file.getSize());
    }

    private void checkFileExtension(String originalFileName) {
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf('.') + 1);
        if (!List.of("doc", "docx", "pdf").contains(fileExtension)) {
            throw new IllegalArgumentException("Unsupported file format");
        }
    }

    private void checkFileSize(Long fileSize) {
        if (fileSize > MAX_FILE_SIZE)
            throw new IllegalArgumentException("The file being uploaded is too large");
    }

    private void attachFileToContent(MultipartFile file, Long lessonId, String key) {
        Content content = lessonService.fetchLessonById(lessonId).getContent();
        AttachmentFile attachmentFile = AttachmentFile.builder()
                .key(key)
                .content(content)
                .originalFileName(file.getOriginalFilename())
                .contentType(file.getContentType())
                .size(file.getSize())
                .build();
        content.getFiles().add(attachmentFile);
        fileRepository.save(attachmentFile);
    }

    @Override
    public String getDownloadUrl(Long id) {
        AttachmentFile file = fetchFileById(id);
        return generateDownloadUrl(file.getKey());
    }

    private String generateDownloadUrl(String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .getObjectRequest(getObjectRequest)
                .signatureDuration(Duration.ofMinutes(30))
                .build();
        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }

    public AttachmentFile fetchFileById(Long id) {
        return fileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(AttachmentFile.class, id));
    }

    @Transactional
    @Override
    public Long deleteFile(Long id) {
        AttachmentFile file = fetchFileById(id);
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(file.getKey())
                .build());
        fileRepository.delete(file);
        return id;
    }
}
