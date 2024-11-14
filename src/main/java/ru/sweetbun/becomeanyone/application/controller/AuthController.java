package ru.sweetbun.becomeanyone.application.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sweetbun.becomeanyone.api.dto.UserDTO;
import ru.sweetbun.becomeanyone.api.port.AuthServicePort;

import static org.springframework.http.ResponseEntity.ok;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthServicePort authServicePort;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO) {
        return ok(authServicePort.register(userDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser() {
        //TODO внедрить JWT
        return ok("Logged in successfully");
    }
}
