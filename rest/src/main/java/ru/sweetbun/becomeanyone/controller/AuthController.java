package ru.sweetbun.becomeanyone.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sweetbun.becomeanyone.contract.AuthService;
import ru.sweetbun.becomeanyone.dto.auth.LoginRequest;
import ru.sweetbun.becomeanyone.dto.token.RefreshTokenRequest;
import ru.sweetbun.becomeanyone.dto.user.request.UserRequest;

import static org.springframework.http.ResponseEntity.ok;

@Tag(name = "Authentication", description = "API для управления регистрацией и авторизацией пользователей")
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Регистрация пользователя", description = "Регистрация нового пользователя в системе")
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRequest userRequest) {
        return ok(authService.register(userRequest));
    }

    @Operation(summary = "Авторизация пользователя", description = "Авторизация пользователя в системе")
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest,
                                       @RequestParam(required = false) boolean rememberMe) {
        return ok(authService.login(loginRequest, rememberMe));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        return ok(authService.logout(authHeader));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestBody RefreshTokenRequest request,
                                                @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        return ok(authService.refreshAccessToken(request, authHeader));
    }
}