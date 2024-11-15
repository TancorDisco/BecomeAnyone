package ru.sweetbun.becomeanyone.port;

import ru.sweetbun.becomeanyone.dto.UserDTO;

public interface AuthServicePort {

    UserDTO register(UserDTO userDTO);
}
