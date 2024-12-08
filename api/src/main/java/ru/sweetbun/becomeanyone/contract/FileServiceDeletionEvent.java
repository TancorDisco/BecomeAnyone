package ru.sweetbun.becomeanyone.contract;

public interface FileServiceDeletionEvent {

    void deleteFileFromCloud(String fileKey);
}
