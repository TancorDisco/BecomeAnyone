package ru.sweetbun.becomeanyone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.sweetbun.becomeanyone.entity.AttachmentFile;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<AttachmentFile, Long> {

    @Query("SELECT f.key FROM AttachmentFile f WHERE f.content.id = :contentId")
    List<String> findFileKeysByContentId(@Param("contentId") Long contentId);
}
