package ru.sweetbun.becomeanyone.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ru.sweetbun.becomeanyone.contract.FileServiceDeletionEvent;
import ru.sweetbun.becomeanyone.dto.message.FileDeletionMessage;

@RequiredArgsConstructor
@Component
public class FileDeletionListener {

    private final FileServiceDeletionEvent fileService;

    @RabbitListener(queues = "fileDeletionQueue")
    public void handleFileDeletion(FileDeletionMessage message) {
        fileService.deleteFileFromCloud(message.getFileKey());
    }
}
