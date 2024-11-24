package ru.sweetbun.becomeanyone.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import ru.sweetbun.becomeanyone.dto.user.response.UserResponse;

@FeignClient(name = "roleService", url = "http://localhost:8080")
public interface RoleServiceClient {

    @PutMapping("users/{userId}/roles/teacher")
    UserResponse appointTeacher(@PathVariable Long userId);

    @PutMapping("users/{userId}/roles/admin")
    UserResponse appointAdmin(@PathVariable Long userId);
}
