package ru.sweetbun.becomeanyone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import ru.sweetbun.becomeanyone.contract.eventpublisher.SendNotificationEventPublisher;
import ru.sweetbun.becomeanyone.dto.message.NotificationMessage;
import ru.sweetbun.becomeanyone.entity.Course;
import ru.sweetbun.becomeanyone.entity.User;
import ru.sweetbun.becomeanyone.repository.CourseRepository;
import ru.sweetbun.becomeanyone.repository.EnrollmentRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class NotificationService {

    private final SendNotificationEventPublisher sendNotificationEventPublisher;

    private final CourseRepository courseRepository;

    private final EnrollmentRepository enrollmentRepository;

    public void notifyAboutNewCourse(Course course) {
        User teacher = course.getTeacher();
        NotificationMessage notification = NotificationMessage.builder()
                .subject("Новый курс от вашего преподавателя")
                .text(teacher.getUsername() + " выпустил новый курс!\n" +
                        "Скорее приступай к учёбе!\n" +
                        "http://217.71.129.139:5148/courses/" + course.getId())
                .build();
        List<Long> courseIds = courseRepository.findCourseIdsByTeacherId(teacher.getId());
        List<User> students = enrollmentRepository.findStudentsByCourseIds(courseIds);

        students.forEach(student -> {
            notification.setTo(student.getEmail());
            sendNotificationEventPublisher.publishSendNotificationEvent(notification);
        });
    }
}
