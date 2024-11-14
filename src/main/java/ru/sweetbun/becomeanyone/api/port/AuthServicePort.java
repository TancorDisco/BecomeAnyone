package ru.sweetbun.becomeanyone.api.port;

import ru.sweetbun.becomeanyone.api.dto.UserDTO;
import ru.sweetbun.becomeanyone.domain.entity.User;

public interface AuthServicePort {

    User register(UserDTO userDTO);
}
