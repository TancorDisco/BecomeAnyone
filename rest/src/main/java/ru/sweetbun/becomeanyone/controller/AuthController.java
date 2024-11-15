package ru.sweetbun.becomeanyone.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sweetbun.becomeanyone.dto.user.request.UserRequest;
import ru.sweetbun.becomeanyone.contract.AuthService;

import static org.springframework.http.ResponseEntity.ok;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRequest userRequest) {
        return ok(authService.register(userRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser() {
        //TODO внедрить JWT
        return ok("Logged in successfully");
    }
}
