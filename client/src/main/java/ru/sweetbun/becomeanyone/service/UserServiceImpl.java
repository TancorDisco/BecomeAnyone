package ru.sweetbun.becomeanyone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import ru.sweetbun.becomeanyone.contract.AuthService;
import ru.sweetbun.becomeanyone.contract.ProfileService;
import ru.sweetbun.becomeanyone.contract.UserService;
import ru.sweetbun.becomeanyone.dto.auth.LoginRequest;
import ru.sweetbun.becomeanyone.dto.profile.ProfileRequest;
import ru.sweetbun.becomeanyone.dto.user.request.UserRequest;
import ru.sweetbun.becomeanyone.dto.user.response.UserResponse;
import ru.sweetbun.becomeanyone.feign.AuthServiceClient;
import ru.sweetbun.becomeanyone.feign.ProfileServiceClient;
import ru.sweetbun.becomeanyone.feign.UserServiceClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, AuthService, ProfileService {

    private final UserServiceClient userServiceClient;
    private final AuthServiceClient authServiceClient;
    private final ProfileServiceClient profileServiceClient;

    public UserResponse register(UserRequest userRequest) {
        return authServiceClient.registerUser(userRequest);
    }

    public String login(LoginRequest loginRequest, boolean rememberMe) {
        return authServiceClient.login(loginRequest, rememberMe);
    }

    public String logout(String authHeader) {
        return authServiceClient.logout(authHeader);
    }

    public UserResponse getUserById(Long id) {
        return userServiceClient.getUserById(id);
    }

    public List<UserResponse> getAllUsers() {
        return userServiceClient.getAllUsers();
    }

    public UserResponse updateUser(UserRequest userRequest, Long id) {
        return userServiceClient.updateUser(id, userRequest);
    }

    public long deleteUserById(Long id) {
        return userServiceClient.deleteUserById(id);
    }

    public UserResponse getCurrentUser() {
        return profileServiceClient.getCurrentUser();
    }

    public UserResponse createUserProfile(ProfileRequest profileRequest) {
        return profileServiceClient.createUserProfile(profileRequest);
    }

    public UserResponse updateUserProfile(ProfileRequest profileRequest) {
        return profileServiceClient.updateUserProfile(profileRequest);
    }
}