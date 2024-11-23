package ru.sweetbun.becomeanyone.contract;

import ru.sweetbun.becomeanyone.dto.auth.LoginRequest;
import ru.sweetbun.becomeanyone.dto.user.request.UserRequest;
import ru.sweetbun.becomeanyone.dto.user.response.UserResponse;

public interface AuthService {

    UserResponse register(UserRequest userRequest);
    String login(LoginRequest loginRequest, boolean rememberMe);
    String logout(String authHeader);
}
