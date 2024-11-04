package ru.sweetbun.BecomeAnyone.exception;

import ru.sweetbun.BecomeAnyone.entity.Course;
import ru.sweetbun.BecomeAnyone.entity.User;

public class ResourceNotFoundException extends RuntimeException{

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String className, Long id) {
        super(className + " not found with id: " + id);
    }

    public ResourceNotFoundException(String className, String title) {
        super(className + " not found with title: " + title);
    }

    public ResourceNotFoundException(String className, User user, Course course) {
        super(className + " not found by parameters such as: " + user.getClass().getSimpleName() + ", "
                + course.getClass().getSimpleName());
    }
}
