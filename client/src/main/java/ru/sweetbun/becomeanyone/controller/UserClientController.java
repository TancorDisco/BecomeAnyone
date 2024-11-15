package ru.sweetbun.becomeanyone.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.sweetbun.becomeanyone.dto.user.UserRequest;
import ru.sweetbun.becomeanyone.dto.user.UserResponse;
import ru.sweetbun.becomeanyone.service.UserServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/client/users")
@RequiredArgsConstructor
public class UserClientController {

    private final UserServiceImpl userService;

    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @PatchMapping("/{id}")
    public UserResponse updateUser(@PathVariable Long id, @RequestBody UserRequest userRequest) {
        return userService.updateUser(userRequest, id);
    }

    @DeleteMapping("/{id}")
    public long deleteUserById(@PathVariable Long id) {
        return userService.deleteUserById(id);
    }
}

