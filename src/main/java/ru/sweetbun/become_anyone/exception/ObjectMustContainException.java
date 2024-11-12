package ru.sweetbun.become_anyone.exception;

public class ObjectMustContainException extends RuntimeException {
    public ObjectMustContainException(String message) {
        super(message);
    }

    public ObjectMustContainException(String mainObject, String subordinateObject) {
        super(mainObject + " must contain at least one " + subordinateObject);
    }
}
