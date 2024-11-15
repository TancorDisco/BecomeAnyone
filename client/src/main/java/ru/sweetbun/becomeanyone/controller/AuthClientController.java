package ru.sweetbun.becomeanyone.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sweetbun.becomeanyone.dto.user.UserRequest;
import ru.sweetbun.becomeanyone.dto.user.UserResponse;
import ru.sweetbun.becomeanyone.service.UserServiceImpl;

@RestController
@RequiredArgsConstructor
@RequestMapping("/client/auth")
public class AuthClientController {

    private final UserServiceImpl userService;

    @PostMapping("/register")
    public UserResponse registerUser(@RequestBody UserRequest userRequest) {
        return userService.register(userRequest);
    }
}
