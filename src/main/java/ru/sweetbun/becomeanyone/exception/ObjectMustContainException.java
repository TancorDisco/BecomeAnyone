package ru.sweetbun.becomeanyone.exception;

public class ObjectMustContainException extends RuntimeException {
    public ObjectMustContainException(String message) {
        super(message);
    }

    public ObjectMustContainException(String mainObject, String subordinateObject) {
        super(mainObject + " must contain at least one " + subordinateObject);
    }
}
