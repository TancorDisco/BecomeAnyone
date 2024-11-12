package ru.sweetbun.become_anyone.exception;

public class ResourceNotFoundException extends RuntimeException{

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(Class<?> resourseClass, Long id) {
        super(resourseClass.getSimpleName() + " not found with id: " + id);
    }

    public ResourceNotFoundException(Class<?> resourseClass,  String title) {
        super(resourseClass.getSimpleName() + " not found with title: " + title);
    }

    public ResourceNotFoundException(Class<?> resourceClass, Object... params) {
        super(generateMessage(resourceClass, params));
    }

    private static String generateMessage(Class<?> resourceClass, Object[] params) {
        StringBuilder message = new StringBuilder(resourceClass.getSimpleName() + " not found by parameters such as: ");
        for (Object param : params) {
            if (param != null) message.append(param.getClass().getSimpleName()).append(", ");
        }
        if (params.length > 0) message.setLength(message.length() - 2);

        return message.toString();
    }
}
