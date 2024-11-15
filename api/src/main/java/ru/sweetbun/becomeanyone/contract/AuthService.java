package ru.sweetbun.becomeanyone.contract;

import ru.sweetbun.becomeanyone.dto.user.UserRequest;
import ru.sweetbun.becomeanyone.dto.user.UserResponse;

public interface AuthService {

    UserResponse register(UserRequest userRequest);
}
