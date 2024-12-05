package ru.sweetbun.becomeanyone.event;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import ru.sweetbun.becomeanyone.contract.eventpublisher.FileDeletionEventPublisher;
import ru.sweetbun.becomeanyone.dto.message.FileDeletionMessage;

@RequiredArgsConstructor
@Component
public class RabbitMQFileDeletionEventPublisher implements FileDeletionEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publishFileDeletionEvent(String fileKey) {
        FileDeletionMessage message = new FileDeletionMessage(fileKey);
        rabbitTemplate.convertAndSend("fileDeletionQueue", message);
    }
}
