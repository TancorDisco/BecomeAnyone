package ru.sweetbun.becomeanyone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import ru.sweetbun.becomeanyone.entity.Course;
import ru.sweetbun.becomeanyone.model.Notification;
import ru.sweetbun.becomeanyone.entity.User;
import ru.sweetbun.becomeanyone.repository.CourseRepository;
import ru.sweetbun.becomeanyone.repository.EnrollmentRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class NotificationService {

    private final RabbitTemplate rabbitTemplate;

    private final CourseRepository courseRepository;

    private final EnrollmentRepository enrollmentRepository;

    public void notifyAboutNewCourse(Course course) {
        User teacher = course.getTeacher();
        Notification notification = new Notification();
        notification.setSubject("Новый курс от вашего преподавателя");
        notification.setText(teacher.getUsername() + " выпустил новый курс!\n" +
                "Скорее приступай к учёбе!");

        List<Long> courseIds = courseRepository.findCourseIdsByTeacherId(teacher.getId());
        List<User> students = enrollmentRepository.findStudentsByCourseIds(courseIds);

        students.forEach(student -> {
            notification.setTo(student.getEmail());
            sendNotification(notification);
        });
    }

    private void sendNotification(Notification notification) {
        rabbitTemplate.convertAndSend("notificationQueue", notification);
    }
}
