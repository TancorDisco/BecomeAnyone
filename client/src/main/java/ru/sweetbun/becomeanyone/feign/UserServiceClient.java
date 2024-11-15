package ru.sweetbun.becomeanyone.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.sweetbun.becomeanyone.dto.profile.ProfileRequest;
import ru.sweetbun.becomeanyone.dto.user.UserRequest;
import ru.sweetbun.becomeanyone.dto.user.UserResponse;

import java.util.List;

@FeignClient(name = "user-service", url = "http://localhost:8080")
public interface UserServiceClient {

    @PostMapping("/auth/register")
    UserResponse registerUser(@RequestBody UserRequest userRequest);

    @GetMapping("/users/{id}")
    UserResponse getUserById(@PathVariable Long id);

    @GetMapping("/users")
    List<UserResponse> getAllUsers();

    @PatchMapping("/users/{id}")
    UserResponse updateUser(@PathVariable Long id, @RequestBody UserRequest userRequest);

    @DeleteMapping("/users/{id}")
    long deleteUserById(@PathVariable Long id);

    @PostMapping("/profile")
    UserResponse createUserProfile(@RequestBody ProfileRequest profileRequest);

    @PatchMapping("/profile")
    UserResponse updateUserProfile(@RequestBody ProfileRequest profileRequest);
}