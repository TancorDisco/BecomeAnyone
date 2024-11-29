package ru.sweetbun.becomeanyone.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sweetbun.becomeanyone.dto.content.ContentRequest;
import ru.sweetbun.becomeanyone.entity.AttachmentFile;
import ru.sweetbun.becomeanyone.entity.Content;
import ru.sweetbun.becomeanyone.entity.Lesson;
import ru.sweetbun.becomeanyone.entity.Video;
import ru.sweetbun.becomeanyone.repository.ContentRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ContentService {

    private final ModelMapper modelMapper;

    private final VideoService videoService;
    @Lazy
    private final FileServiceImpl fileService;

    private final ContentRepository contentRepository;

    @Transactional
    public Content createContent(Lesson lesson) {
        Content content = Content.builder().lesson(lesson).build();
        return contentRepository.save(content);
    }

    @Transactional
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

    public void deleteAllFiles(List<AttachmentFile> files) {
        files.forEach(file -> fileService.deleteFile(file.getId()));
    }
}
