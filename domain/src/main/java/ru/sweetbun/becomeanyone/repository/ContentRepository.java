package ru.sweetbun.becomeanyone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sweetbun.becomeanyone.entity.Content;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {
}
