package ru.sweetbun.becomeanyone.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sweetbun.becomeanyone.contract.UserService;

import static java.util.Optional.ofNullable;
import static org.springframework.http.ResponseEntity.ok;

@Tag(name = "User Management", description = "API для управления пользователями")
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    @Operation(summary = "Получить всех пользователей", description = "Возвращает список всех пользователей системы")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.of(ofNullable(userService.getAllUsers()));
    }

    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    @GetMapping("{id}")
    @Operation(summary = "Получить профиль пользователя по ID", description = "Возвращает профиль пользователя по его уникальному ID")
    public ResponseEntity<?> getUserProfileById(@PathVariable Long id) {
        return ok(userService.getUserById(id));
    }
}
