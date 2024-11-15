package ru.sweetbun.becomeanyone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import ru.sweetbun.becomeanyone.dto.profile.ProfileRequest;
import ru.sweetbun.becomeanyone.dto.user.UserRequest;
import ru.sweetbun.becomeanyone.contract.AuthService;
import ru.sweetbun.becomeanyone.contract.ProfileService;
import ru.sweetbun.becomeanyone.contract.UserService;
import ru.sweetbun.becomeanyone.dto.user.UserResponse;
import ru.sweetbun.becomeanyone.feign.UserServiceClient;

import java.util.List;

@Profile("feign")
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, ProfileService, AuthService {

    private final UserServiceClient userServiceClient;

    @Override
    public UserResponse register(UserRequest userRequest) {
        return userServiceClient.registerUser(userRequest);
    }

    @Override
    public UserResponse getUserById(Long id) {
        return userServiceClient.getUserById(id);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userServiceClient.getAllUsers();
    }

    @Override
    public UserResponse getUserByUsername(String username) {
        return null;
    }

    @Override
    public UserResponse updateUser(UserRequest userRequest, Long id) {
        return userServiceClient.updateUser(id, userRequest);
    }

    @Override
    public long deleteUserById(Long id) {
        return userServiceClient.deleteUserById(id);
    }

    @Override
    public UserResponse getCurrentUser() {
        return null;
    }

    @Override
    public UserResponse createUserProfile(ProfileRequest profileRequest) {
        return userServiceClient.createUserProfile(profileRequest);
    }

    @Override
    public UserResponse updateUserProfile(ProfileRequest profileRequest) {
        return userServiceClient.updateUserProfile(profileRequest);
    }
}