package ru.sweetbun.becomeanyone.port;

import ru.sweetbun.becomeanyone.dto.ProfileDTO;
import ru.sweetbun.becomeanyone.dto.UserDTO;

public interface ProfileServicePort {

    UserDTO getCurrentUser();
    UserDTO createUserProfile(ProfileDTO profileDTO);
    UserDTO updateUserProfile(ProfileDTO profileDTO);
}
