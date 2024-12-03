package ru.sweetbun.becomeanyone.util;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ru.sweetbun.becomeanyone.entity.Notification;
import ru.sweetbun.becomeanyone.service.EmailService;

@RequiredArgsConstructor
@Component
public class NotificationListener {

    private final EmailService emailService;

    @RabbitListener(queues = "notificationQueue")
    public void processNotification(Notification notification) {
        emailService.sendEmail(notification);
    }
}
