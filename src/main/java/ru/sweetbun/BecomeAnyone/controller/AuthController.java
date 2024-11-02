package ru.sweetbun.BecomeAnyone.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sweetbun.BecomeAnyone.DTO.UserDTO;
import ru.sweetbun.BecomeAnyone.service.UserService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.register(userDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser() {
        //TODO внедрить JWT
        return ResponseEntity.ok("Logged in successfully");
    }
}
