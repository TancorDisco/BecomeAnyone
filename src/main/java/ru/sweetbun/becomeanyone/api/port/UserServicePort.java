package ru.sweetbun.becomeanyone.api.port;

import ru.sweetbun.becomeanyone.api.dto.UserDTO;
import ru.sweetbun.becomeanyone.domain.entity.User;

import java.util.List;

public interface UserServicePort {

    User getUserById(Long id);
    List<User> getAllUsers();
    User getUserByUsername(String username);
    User updateUser(UserDTO userDTO, Long id);
    long deleteUserById(Long id);
}
