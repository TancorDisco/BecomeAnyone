package ru.sweetbun.becomeanyone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sweetbun.becomeanyone.entity.AttachmentFile;

@Repository
public interface FileRepository extends JpaRepository<AttachmentFile, Long> {
}
