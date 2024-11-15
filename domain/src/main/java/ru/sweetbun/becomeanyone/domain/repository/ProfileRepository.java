package ru.sweetbun.becomeanyone.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sweetbun.becomeanyone.domain.entity.Profile;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
}
