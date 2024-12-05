package ru.sweetbun.becomeanyone.contract.eventpublisher;

public interface FileDeletionEventPublisher {

    void publishFileDeletionEvent(String fileKey);
}
