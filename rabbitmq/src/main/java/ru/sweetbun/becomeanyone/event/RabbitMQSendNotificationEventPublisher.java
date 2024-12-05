package ru.sweetbun.becomeanyone.event;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import ru.sweetbun.becomeanyone.contract.eventpublisher.SendNotificationEventPublisher;
import ru.sweetbun.becomeanyone.dto.message.NotificationMessage;

@RequiredArgsConstructor
@Service
public class RabbitMQSendNotificationEventPublisher implements SendNotificationEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publishSendNotificationEvent(NotificationMessage message) {
        rabbitTemplate.convertAndSend("notificationQueue", message);
    }
}
