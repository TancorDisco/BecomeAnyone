package ru.sweetbun.becomeanyone.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sweetbun.becomeanyone.domain.entity.Video;
import ru.sweetbun.becomeanyone.infrastructure.repository.VideoRepository;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
@Service
public class VideoService {

    private final VideoRepository videoRepository;

    public Video updateVideo(String videoUrl, Video video) {
        if (video == null) video = new Video();

        String platform = identifyPlatform(videoUrl);
        String videoId = extractVideoId(videoUrl, platform);
        if (videoId == null)
            throw new IllegalArgumentException("Invalid video URL: " + videoUrl);
        String accessKey = extractAccessKey(videoUrl, platform);

        video.setPlatform(platform);
        video.setVideoId(videoId);
        video.setAccessKey(accessKey);
        return video;
    }

    private String identifyPlatform(String videoUrl) {
        if (videoUrl.contains("youtube.com") || videoUrl.contains("youtu.be")) {
            return "youtube";
        } else if (videoUrl.contains("rutube.ru")) {
            return "rutube";
        }
        throw new IllegalArgumentException("Unsupported video platform");
    }

    private String extractVideoId(String videoUrl, String platform) {
        switch (platform) {
            case "youtube":
                String youtubePattern = "^(?:https?://)?(?:www\\.)?(?:youtube\\.com/watch\\?v=|youtu\\.be/)([\\w\\-]+)";
                return extractWithPattern(videoUrl, youtubePattern);
            case "rutube":
                String rutubePattern = "^(?:https?://)?(?:www\\.)?rutube\\.ru/(?:video/private/|video/|play/embed/|video/)([\\w]+)";
                return extractWithPattern(videoUrl, rutubePattern);
            default:
                return null;
        }
    }

    private String extractWithPattern(String url, String pattern) {
        Pattern compiledPattern = Pattern.compile(pattern);
        log.info("Pattern: {}", compiledPattern);
        Matcher matcher = compiledPattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private String extractAccessKey(String videoUrl, String platform) {
        if ("rutube".equals(platform)) {
            String accessKeyPattern = "\\?p=([\\w_\\-]+)";
            return extractWithPattern(videoUrl, accessKeyPattern);
        }
        return null;
    }

    @Transactional
    public void deleteVideo(Video video) {
        videoRepository.delete(video);
    }
}
