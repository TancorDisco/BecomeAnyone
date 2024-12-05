package ru.sweetbun.becomeanyone.contract;

public interface FileDeletionEventPublisher {

    void publishFileDeletionEvent(String fileKey);
}
