package ru.sweetbun.becomeanyone.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.sweetbun.becomeanyone.dto.ProfileDTO;
import ru.sweetbun.becomeanyone.dto.UserDTO;

import java.util.List;

@FeignClient(name = "user-service", url = "http://localhost:8080")
public interface UserServiceClient {

    @PostMapping("/auth/register")
    UserDTO registerUser(@RequestBody UserDTO userDTO);

    @GetMapping("/users/{id}")
    UserDTO getUserById(@PathVariable Long id);

    @GetMapping("/users")
    List<UserDTO> getAllUsers();

    @PatchMapping("/users/{id}")
    UserDTO updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO);

    @DeleteMapping("/users/{id}")
    long deleteUserById(@PathVariable Long id);

    @PostMapping("/profile")
    UserDTO createUserProfile(@RequestBody ProfileDTO profileDTO);

    @PatchMapping("/profile")
    UserDTO updateUserProfile(@RequestBody ProfileDTO profileDTO);
}