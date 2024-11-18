package ru.sweetbun.becomeanyone.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.sweetbun.becomeanyone.dto.user.request.UserRequest;
import ru.sweetbun.becomeanyone.dto.user.response.UserResponse;

import java.util.List;

@RequestMapping("/users")
@FeignClient(name = "userService", url = "http://localhost:8080")
public interface UserServiceClient {

    @GetMapping("{id}")
    UserResponse getUserById(@PathVariable Long id);

    @GetMapping
    List<UserResponse> getAllUsers();

    @PatchMapping("{id}")
    UserResponse updateUser(@PathVariable Long id, @RequestBody UserRequest userRequest);

    @DeleteMapping("{id}")
    long deleteUserById(@PathVariable Long id);
}