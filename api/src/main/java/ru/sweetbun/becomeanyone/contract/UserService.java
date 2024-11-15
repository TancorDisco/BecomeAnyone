package ru.sweetbun.becomeanyone.contract;


import ru.sweetbun.becomeanyone.dto.user.request.UserRequest;
import ru.sweetbun.becomeanyone.dto.user.response.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse getUserById(Long id);
    List<UserResponse> getAllUsers();
    UserResponse updateUser(UserRequest userRequest, Long id);
    long deleteUserById(Long id);
}
