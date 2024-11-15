package ru.sweetbun.becomeanyone.contract;

import ru.sweetbun.becomeanyone.dto.profile.ProfileRequest;
import ru.sweetbun.becomeanyone.dto.user.UserResponse;

public interface ProfileService {

    UserResponse getCurrentUser();
    UserResponse createUserProfile(ProfileRequest profileRequest);
    UserResponse updateUserProfile(ProfileRequest profileRequest);
}
