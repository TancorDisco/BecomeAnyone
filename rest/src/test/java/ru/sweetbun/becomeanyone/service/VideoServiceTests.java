package ru.sweetbun.becomeanyone.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.sweetbun.becomeanyone.entity.Video;
import ru.sweetbun.becomeanyone.repository.VideoRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class VideoServiceTests {

    @Mock
    private VideoRepository videoRepository;

    @InjectMocks
    private VideoService videoService;

    private Video video;

    @BeforeEach
    void setUp() {
        video = new Video();
    }

    @ParameterizedTest
    @CsvSource({
            "https://www.youtube.com/watch?v=abc123, youtube, abc123, null",
            "https://youtu.be/abc123, youtube, abc123, null",
            "https://rutube.ru/video/def456, rutube, def456, null",
            "https://rutube.ru/video/private/xyz789?p=accessKey123, rutube, xyz789, accessKey123"
    })
    void updateVideo_ValidUrl_ShouldReturnUpdatedVideo(String videoUrl, String expectedPlatform, String expectedVideoId, String expectedAccessKey) {
        Video result = videoService.updateVideo(videoUrl, video);
        String actualAccessKey = "null".equals(expectedAccessKey) ? null : expectedAccessKey;

        assertEquals(expectedPlatform, result.getPlatform());
        assertEquals(expectedVideoId, result.getVideoId());
        assertEquals(actualAccessKey, result.getAccessKey());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "https://example.com/watch?v=abc123",
            "https://rutube.ru/video/",
            "https://youtu.be/"
    })
    void updateVideo_InvalidUrl_ShouldThrowIllegalArgumentException(String videoUrl) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> videoService.updateVideo(videoUrl, video));

        assertTrue(exception.getMessage().contains("Invalid video URL") || exception.getMessage().contains("Unsupported video platform"));
    }

    @Test
    void updateVideo_NullVideo_ShouldCreateNewVideo() {
        String videoUrl = "https://www.youtube.com/watch?v=abc123";
        Video result = videoService.updateVideo(videoUrl, null);

        assertNotNull(result);
        assertEquals("youtube", result.getPlatform());
        assertEquals("abc123", result.getVideoId());
        assertNull(result.getAccessKey());
    }

    @Test
    void deleteVideo_ValidVideo_ShouldCallRepositoryDelete() {
        videoService.deleteVideo(video);

        verify(videoRepository, times(1)).delete(video);
    }
}
