package ru.sweetbun.becomeanyone.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sweetbun.becomeanyone.domain.entity.SocialLink;

@Repository
public interface SocialLinkRepository extends JpaRepository<SocialLink, Long> {
}
