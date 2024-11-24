package ru.sweetbun.becomeanyone.contract;

import ru.sweetbun.becomeanyone.dto.user.response.UserResponse;

public interface RoleService {

    UserResponse appointTeacher(Long userId);
    UserResponse appointAdmin(Long userId);
}
