package ru.sweetbun.becomeanyone.contract;


import ru.sweetbun.becomeanyone.dto.user.UserRequest;
import ru.sweetbun.becomeanyone.dto.user.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse getUserById(Long id);
    List<UserResponse> getAllUsers();
    UserResponse updateUser(UserRequest userRequest, Long id);
    long deleteUserById(Long id);
}
