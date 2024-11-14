package ru.sweetbun.becomeanyone.api.port;

import ru.sweetbun.becomeanyone.api.dto.ProfileDTO;
import ru.sweetbun.becomeanyone.domain.entity.User;

public interface ProfileServicePort {

    User getCurrentUser();
    User createUserProfile(ProfileDTO profileDTO);
    User updateUserProfile(ProfileDTO profileDTO);
}
