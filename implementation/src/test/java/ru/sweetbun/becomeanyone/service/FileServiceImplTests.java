package ru.sweetbun.becomeanyone.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.sweetbun.becomeanyone.entity.AttachmentFile;
import ru.sweetbun.becomeanyone.entity.Content;
import ru.sweetbun.becomeanyone.entity.Lesson;
import ru.sweetbun.becomeanyone.exception.ResourceNotFoundException;
import ru.sweetbun.becomeanyone.repository.FileRepository;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceImplTests {

    @Mock
    private S3Client s3Client;

    @Mock
    private S3Presigner s3Presigner;

    @Mock
    private FileRepository fileRepository;

    @Mock
    private LessonServiceImpl lessonService;

    @InjectMocks
    private FileServiceImpl fileService;

    private static final String BUCKET_NAME = "test-bucket";
    private static final Long MAX_FILE_SIZE = 10_000_000L;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(fileService, "BUCKET_NAME", BUCKET_NAME);
        ReflectionTestUtils.setField(fileService, "MAX_FILE_SIZE", MAX_FILE_SIZE);
    }

    @Test
    void uploadFile_ValidFile_SuccessfulUpload() throws IOException {
        // Arrange
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("document.pdf");
        when(file.getSize()).thenReturn(1_000_000L);

        Path tempFile = Files.createTempFile("test-", ".tmp");
        when(file.getInputStream()).thenReturn(Files.newInputStream(tempFile));

        Content content = mock(Content.class);
        Lesson lesson = mock(Lesson.class);
        when(lesson.getContent()).thenReturn(content);
        when(lessonService.fetchLessonById(anyLong())).thenReturn(lesson);

        // Act
        String result = fileService.uploadFile(file, 1L);

        // Assert
        verify(s3Client).putObject(any(PutObjectRequest.class), any(Path.class));
        verify(fileRepository).save(any(AttachmentFile.class));
        assertNotNull(result);
        assertTrue(result.contains("document.pdf"));
    }


    @Test
    void uploadFile_FileIsTooLarge_ThrowsException() throws IOException {
        // Arrange
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("document.pdf");
        when(file.getSize()).thenReturn(MAX_FILE_SIZE + 1);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> fileService.uploadFile(file, 1L));
        assertEquals("The file being uploaded is too large", exception.getMessage());
    }

    @Test
    void uploadFile_InvalidExtension_ThrowsException() throws IOException {
        // Arrange
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("document.dshfjdsh");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> fileService.uploadFile(file, 1L));
        assertEquals("Unsupported file format", exception.getMessage());
    }

    @Test
    void getDownloadUrl_ValidId_ReturnsUrl() throws Exception {
        // Arrange
        AttachmentFile file = AttachmentFile.builder().key("test-key").build();
        when(fileRepository.findById(anyLong())).thenReturn(Optional.of(file));

        PresignedGetObjectRequest presignedRequest = mock(PresignedGetObjectRequest.class);
        when(presignedRequest.url()).thenReturn(new URI("http://example.com").toURL());

        when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class))).thenReturn(presignedRequest);

        // Act
        String url = fileService.getDownloadUrl(1L);

        // Assert
        assertNotNull(url);
        assertEquals("http://example.com", url);
        verify(fileRepository).findById(1L);
    }


    @Test
    void getDownloadUrl_FileNotFound_ThrowsException() {
        // Arrange
        when(fileRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> fileService.getDownloadUrl(1L));
        assertTrue(exception.getMessage().contains("AttachmentFile"));
    }

    @Test
    void deleteFile_ValidId_DeletesSuccessfully() {
        // Arrange
        AttachmentFile file = AttachmentFile.builder().key("test-key").build();
        when(fileRepository.findById(anyLong())).thenReturn(Optional.of(file));

        // Act
        Long result = fileService.deleteFile(1L);

        // Assert
        assertEquals(1L, result);
        verify(s3Client).deleteObject(any(DeleteObjectRequest.class));
        verify(fileRepository).delete(file);
    }

    @Test
    void deleteFile_FileNotFound_ThrowsException() {
        // Arrange
        when(fileRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> fileService.deleteFile(1L));
        assertTrue(exception.getMessage().contains("AttachmentFile"));
    }
}
