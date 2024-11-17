package ru.sweetbun.becomeanyone.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.sweetbun.becomeanyone.dto.user.request.UserRequest;
import ru.sweetbun.becomeanyone.dto.user.response.UserResponse;

@FeignClient(name = "authService", url = "http://localhost:8080")
public interface AuthServiceClient {

    @PostMapping("/auth/register")
    UserResponse registerUser(@RequestBody UserRequest userRequest);
}
