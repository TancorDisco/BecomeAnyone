package ru.sweetbun.becomeanyone.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.sweetbun.becomeanyone.dto.ContentDTO;
import ru.sweetbun.becomeanyone.entity.Content;
import ru.sweetbun.becomeanyone.entity.Video;
import ru.sweetbun.becomeanyone.repository.ContentRepository;

@RequiredArgsConstructor
@Service
public class ContentService {

    private final ContentRepository contentRepository;

    private final ModelMapper modelMapper;

    private final VideoService videoService;

    public Content updateContent(ContentDTO contentDTO, Content content) {
        if (content == null) content = new Content();
        modelMapper.map(contentDTO, content);
        Video currentVideo = content.getVideo();
        String videoUrl = contentDTO.getVideoUrl();

        if (videoUrl != null)  {
            Video video = videoService.updateVideo(videoUrl, currentVideo);
            video.setContent(content);
            content.setVideo(video);
        } else if (currentVideo != null) {
            videoService.deleteVideo(currentVideo);
        }

        return content;
    }
}
