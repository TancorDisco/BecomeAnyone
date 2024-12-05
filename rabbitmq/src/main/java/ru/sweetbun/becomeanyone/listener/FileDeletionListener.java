package ru.sweetbun.becomeanyone.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ru.sweetbun.becomeanyone.contract.FileServiceRabbitMq;
import ru.sweetbun.becomeanyone.dto.rabbitmq.FileDeletionMessage;

@RequiredArgsConstructor
@Component
public class FileDeletionListener {

    private final FileServiceRabbitMq fileService;

    @RabbitListener(queues = "fileDeletionQueue")
    public void handleFileDeletion(FileDeletionMessage message) {
        fileService.deleteFileFromCloud(message.getFileKey());
    }
}
