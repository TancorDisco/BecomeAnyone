package ru.sweetbun.becomeanyone.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.sweetbun.becomeanyone.contract.eventpublisher.SendNotificationEventPublisher;
import ru.sweetbun.becomeanyone.dto.message.NotificationMessage;
import ru.sweetbun.becomeanyone.entity.Course;
import ru.sweetbun.becomeanyone.entity.User;
import ru.sweetbun.becomeanyone.repository.CourseRepository;
import ru.sweetbun.becomeanyone.repository.EnrollmentRepository;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTests {

    @Mock
    private SendNotificationEventPublisher sendNotificationEventPublisher;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @InjectMocks
    private NotificationService notificationService;

    private User teacher;
    private Course course;

    @BeforeEach
    void setUp() {
        teacher = User.builder().id(1L).username("teacher").build();
        course = Course.builder().id(100L).teacher(teacher).build();
    }

    @Test
    void notifyAboutNewCourse_WithStudents_ShouldPublishNotifications() {
        // Arrange
        User student1 = User.builder().email("student1@example.com").build();
        User student2 = User.builder().email("student2@example.com").build();

        List<Long> courseIds = List.of(100L, 101L);
        List<User> students = List.of(student1, student2);

        when(courseRepository.findCourseIdsByTeacherId(teacher.getId())).thenReturn(courseIds);
        when(enrollmentRepository.findStudentsByCourseIds(courseIds)).thenReturn(students);

        // Act
        notificationService.notifyAboutNewCourse(course);

        // Assert
        verify(sendNotificationEventPublisher, times(2)).publishSendNotificationEvent(any(NotificationMessage.class));
        verify(courseRepository, times(1)).findCourseIdsByTeacherId(teacher.getId());
        verify(enrollmentRepository, times(1)).findStudentsByCourseIds(courseIds);
    }

    @Test
    void notifyAboutNewCourse_NoStudents_ShouldNotPublishNotifications() {
        // Arrange
        List<Long> courseIds = List.of(100L, 101L);
        List<User> students = List.of();

        when(courseRepository.findCourseIdsByTeacherId(teacher.getId())).thenReturn(courseIds);
        when(enrollmentRepository.findStudentsByCourseIds(courseIds)).thenReturn(students);

        // Act
        notificationService.notifyAboutNewCourse(course);

        // Assert
        verify(sendNotificationEventPublisher, never()).publishSendNotificationEvent(any(NotificationMessage.class));
        verify(courseRepository, times(1)).findCourseIdsByTeacherId(teacher.getId());
        verify(enrollmentRepository, times(1)).findStudentsByCourseIds(courseIds);
    }
}
