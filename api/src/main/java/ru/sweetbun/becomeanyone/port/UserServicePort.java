package ru.sweetbun.becomeanyone.port;


import ru.sweetbun.becomeanyone.dto.UserDTO;

import java.util.List;

public interface UserServicePort {

    UserDTO getUserById(Long id);
    List<UserDTO> getAllUsers();
    UserDTO getUserByUsername(String username);
    UserDTO updateUser(UserDTO userDTO, Long id);
    long deleteUserById(Long id);
}
