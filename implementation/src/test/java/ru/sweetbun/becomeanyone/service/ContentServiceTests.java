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
import ru.sweetbun.becomeanyone.dto.content.ContentDTO;
import ru.sweetbun.becomeanyone.domain.entity.Content;
import ru.sweetbun.becomeanyone.domain.entity.Video;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContentServiceTests {

    private final ModelMapper modelMapper = ModelMapperConfig.createConfiguredModelMapper();

    @Mock
    private VideoService videoService;

    @InjectMocks
    private ContentService contentService;

    private ContentDTO contentDTO;
    private Content content;
    private Video video;

    @BeforeEach
    void setUp() {
        contentService = new ContentService(modelMapper, videoService);

        contentDTO = new ContentDTO();
        content = new Content();
        video = new Video();
    }

    @Test
    void updateContent_ContentAndVideoUrlProvided_ContentUpdatedWithNewVideo() {
        // Arrange
        contentDTO.setVideoUrl("newVideoUrl");
        content.setVideo(video);
        when(videoService.updateVideo("newVideoUrl", video)).thenReturn(video);

        // Act
        Content result = contentService.updateContent(contentDTO, content);

        // Assert
        verify(videoService).updateVideo("newVideoUrl", video);
        assertEquals(video, result.getVideo());
        assertEquals(content, video.getContent());
    }

    @Test
    void updateContent_ContentProvidedAndVideoUrlNull_VideoDeleted() {
        // Arrange
        content.setVideo(video);
        contentDTO.setVideoUrl(null);

        // Act
        Content result = contentService.updateContent(contentDTO, content);

        // Assert
        verify(videoService).deleteVideo(video);
        assertNull(result.getVideo());
    }

    @Test
    void updateContent_NullContent_ExpectedNewContentCreated() {
        // Arrange
        contentDTO.setVideoUrl("newVideoUrl");
        when(videoService.updateVideo("newVideoUrl", null)).thenReturn(video);

        // Act
        Content result = contentService.updateContent(contentDTO, null);

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
        contentDTO.setVideoUrl(videoUrl);

        // Act
        Content result = contentService.updateContent(contentDTO, content);

        // Assert
        verify(videoService).deleteVideo(video);
        assertNull(result.getVideo());
    }

    @Test
    void updateContent_NoVideoAndNoVideoUrl_VideoRemainsNull() {
        // Arrange
        contentDTO.setVideoUrl(null);

        // Act
        Content result = contentService.updateContent(contentDTO, content);

        // Assert
        verify(videoService, never()).deleteVideo(any());
        verify(videoService, never()).updateVideo(any(), any());
        assertNull(result.getVideo());
    }
}