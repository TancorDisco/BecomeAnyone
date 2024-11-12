package ru.sweetbun.become_anyone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sweetbun.become_anyone.entity.SocialLink;

@Repository
public interface SocialLinkRepository extends JpaRepository<SocialLink, Long> {
}
