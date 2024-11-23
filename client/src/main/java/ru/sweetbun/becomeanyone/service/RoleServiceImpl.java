package ru.sweetbun.becomeanyone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sweetbun.becomeanyone.contract.RoleService;
import ru.sweetbun.becomeanyone.dto.user.response.UserResponse;
import ru.sweetbun.becomeanyone.feign.RoleServiceClient;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleServiceClient roleServiceClient;

    @Override
    public UserResponse appointTeacher(Long userId) {
        return roleServiceClient.appointTeacher(userId);
    }

    @Override
    public UserResponse appointAdmin(Long userId) {
        return roleServiceClient.appointAdmin(userId);
    }
}
