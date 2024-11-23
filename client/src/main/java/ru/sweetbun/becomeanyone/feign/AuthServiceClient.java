package ru.sweetbun.becomeanyone.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import ru.sweetbun.becomeanyone.dto.auth.LoginRequest;
import ru.sweetbun.becomeanyone.dto.user.request.UserRequest;
import ru.sweetbun.becomeanyone.dto.user.response.UserResponse;

@FeignClient(name = "authService", url = "http://localhost:8080")
public interface AuthServiceClient {

    @PostMapping("/auth/register")
    UserResponse registerUser(@RequestBody UserRequest userRequest);

    @PostMapping("/auth/login")
    String login(@RequestBody LoginRequest loginRequest, @RequestParam boolean rememberMe);

    @PostMapping("/auth/logout")
    String logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader);
}
