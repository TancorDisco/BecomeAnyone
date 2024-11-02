package ru.sweetbun.BecomeAnyone.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.sweetbun.BecomeAnyone.entity.Profile;

@Data
@AllArgsConstructor
public class UserDTO {
    private String username;
    private String email;
    private String password;
}
