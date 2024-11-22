package ru.sweetbun.becomeanyone.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sweetbun.becomeanyone.contract.AuthService;
import ru.sweetbun.becomeanyone.dto.user.request.UserRequest;

@Tag(name = "Authentication", description = "API для управления регистрацией и авторизацией пользователей")
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Регистрация пользователя", description = "Регистрация нового пользователя в системе")
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @Parameter(description = "Данные пользователя для регистрации")
            @RequestBody UserRequest userRequest) {
        return ResponseEntity.ok(authService.register(userRequest));
    }

    @Operation(summary = "Авторизация пользователя", description = "Авторизация пользователя в системе")
    @PostMapping("/login")
    public ResponseEntity<?> loginUser() {
        //TODO внедрить JWT
        return ResponseEntity.ok("Logged in successfully");
    }
}