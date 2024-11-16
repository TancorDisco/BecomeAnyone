package ru.sweetbun.becomeanyone.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.sweetbun.becomeanyone.dto.content.ContentRequest;
import ru.sweetbun.becomeanyone.domain.entity.Content;
import ru.sweetbun.becomeanyone.domain.entity.Video;

@RequiredArgsConstructor
@Service
public class ContentService {

    private final ModelMapper modelMapper;

    private final VideoService videoService;

    public Content updateContent(ContentRequest contentRequest, Content content) {
        if (content == null) content = new Content();
        modelMapper.map(contentRequest, content);
        Video currentVideo = content.getVideo();
        String videoUrl = contentRequest.getVideoUrl();

        if (videoUrl != null && !videoUrl.isBlank())  {
            Video video = videoService.updateVideo(videoUrl, currentVideo);
            video.setContent(content);
            content.setVideo(video);
        } else if (currentVideo != null) {
            videoService.deleteVideo(currentVideo);
            content.setVideo(null);
        }

        return content;
    }
}
