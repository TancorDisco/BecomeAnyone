package ru.sweetbun.BecomeAnyone.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sweetbun.BecomeAnyone.DTO.UserDTO;
import ru.sweetbun.BecomeAnyone.service.UserService;

import static org.springframework.http.ResponseEntity.ok;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO) {
        return ok(userService.register(userDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser() {
        //TODO внедрить JWT
        return ok("Logged in successfully");
    }
}
