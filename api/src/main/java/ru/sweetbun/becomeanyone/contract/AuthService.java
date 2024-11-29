package ru.sweetbun.becomeanyone.contract;

import ru.sweetbun.becomeanyone.dto.auth.LoginRequest;
import ru.sweetbun.becomeanyone.dto.token.RefreshTokenRequest;
import ru.sweetbun.becomeanyone.dto.user.request.UserRequest;
import ru.sweetbun.becomeanyone.dto.user.response.UserResponse;

import java.util.Map;

public interface AuthService {

    UserResponse register(UserRequest userRequest);
    Map<String, String> login(LoginRequest loginRequest, boolean rememberMe);
    String logout(String authHeader);
    Map<String, String> refreshAccessToken(RefreshTokenRequest refreshToken, String authHeader);
}
