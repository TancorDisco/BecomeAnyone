package ru.sweetbun.becomeanyone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import ru.sweetbun.becomeanyone.dto.ProfileDTO;
import ru.sweetbun.becomeanyone.dto.UserDTO;
import ru.sweetbun.becomeanyone.port.AuthServicePort;
import ru.sweetbun.becomeanyone.port.ProfileServicePort;
import ru.sweetbun.becomeanyone.port.UserServicePort;
import ru.sweetbun.becomeanyone.feign.UserServiceClient;

import java.util.List;

@Profile("feign")
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserServicePort, ProfileServicePort, AuthServicePort {

    private final UserServiceClient userServiceClient;

    @Override
    public UserDTO register(UserDTO userDTO) {
        return userServiceClient.registerUser(userDTO);
    }

    @Override
    public UserDTO getUserById(Long id) {
        return userServiceClient.getUserById(id);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userServiceClient.getAllUsers();
    }

    @Override
    public UserDTO getUserByUsername(String username) {
        return null;
    }

    @Override
    public UserDTO updateUser(UserDTO userDTO, Long id) {
        return userServiceClient.updateUser(id, userDTO);
    }

    @Override
    public long deleteUserById(Long id) {
        return userServiceClient.deleteUserById(id);
    }

    @Override
    public UserDTO getCurrentUser() {
        return null;
    }

    @Override
    public UserDTO createUserProfile(ProfileDTO profileDTO) {
        return userServiceClient.createUserProfile(profileDTO);
    }

    @Override
    public UserDTO updateUserProfile(ProfileDTO profileDTO) {
        return userServiceClient.updateUserProfile(profileDTO);
    }
}