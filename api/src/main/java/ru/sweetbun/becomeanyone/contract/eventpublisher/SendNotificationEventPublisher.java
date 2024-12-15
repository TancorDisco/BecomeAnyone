package ru.sweetbun.becomeanyone.contract.eventpublisher;

import ru.sweetbun.becomeanyone.dto.message.NotificationMessage;

public interface SendNotificationEventPublisher {

    void publishSendNotificationEvent(NotificationMessage message);
}
