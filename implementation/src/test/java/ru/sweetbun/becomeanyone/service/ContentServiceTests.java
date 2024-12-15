package ru.sweetbun.becomeanyone.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import ru.sweetbun.becomeanyone.config.ModelMapperConfig;
import ru.sweetbun.becomeanyone.dto.content.ContentRequest;
import ru.sweetbun.becomeanyone.entity.Content;
import ru.sweetbun.becomeanyone.entity.Video;
import ru.sweetbun.becomeanyone.repository.ContentRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContentServiceTests {

    private final ModelMapper modelMapper = ModelMapperConfig.createConfiguredModelMapper();

    @Mock
    private VideoService videoService;

    @Mock
    private ContentRepository contentRepository;

    @InjectMocks
    private ContentService contentService;

    private ContentRequest contentRequest;
    private Content content;
    private Video video;

    @BeforeEach
    void setUp() {
        contentService = new ContentService(modelMapper, videoService, contentRepository);

        contentRequest = new ContentRequest();
        content = new Content();
        video = new Video();
    }

    @Test
    void updateContent_ContentAndVideoUrlProvided_ContentUpdatedWithNewVideo() {
        // Arrange
        contentRequest.setVideoUrl("newVideoUrl");
        content.setVideo(video);
        when(videoService.updateVideo("newVideoUrl", video)).thenReturn(video);

        // Act
        Content result = contentService.updateContent(contentRequest, content);

        // Assert
        verify(videoService).updateVideo("newVideoUrl", video);
        assertEquals(video, result.getVideo());
        assertEquals(content, video.getContent());
    }

    @Test
    void updateContent_ContentProvidedAndVideoUrlNull_VideoDeleted() {
        // Arrange
        content.setVideo(video);
        contentRequest.setVideoUrl(null);

        // Act
        Content result = contentService.updateContent(contentRequest, content);

        // Assert
        verify(videoService).deleteVideo(video);
        assertNull(result.getVideo());
    }

    @Test
    void updateContent_NullContent_ExpectedNewContentCreated() {
        // Arrange
        contentRequest.setVideoUrl("newVideoUrl");
        when(videoService.updateVideo("newVideoUrl", null)).thenReturn(video);

        // Act
        Content result = contentService.updateContent(contentRequest, null);

        // Assert
        assertNotNull(result);
        assertEquals(video, result.getVideo());
        assertEquals(result, video.getContent());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  "})
    void updateContent_VideoUrlEmptyOrNull_VideoDeletedIfPresent(String videoUrl) {
        // Arrange
        content.setVideo(video);
        contentRequest.setVideoUrl(videoUrl);

        // Act
        Content result = contentService.updateContent(contentRequest, content);

        // Assert
        verify(videoService).deleteVideo(video);
        assertNull(result.getVideo());
    }

    @Test
    void updateContent_NoVideoAndNoVideoUrl_VideoRemainsNull() {
        // Arrange
        contentRequest.setVideoUrl(null);

        // Act
        Content result = contentService.updateContent(contentRequest, content);

        // Assert
        verify(videoService, never()).deleteVideo(any());
        verify(videoService, never()).updateVideo(any(), any());
        assertNull(result.getVideo());
    }
}