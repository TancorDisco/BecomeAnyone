package ru.sweetbun.becomeanyone.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sweetbun.becomeanyone.contract.ProfileService;
import ru.sweetbun.becomeanyone.dto.profile.ProfileRequest;

import static org.springframework.http.ResponseEntity.ok;

@Tag(name = "Profile Management", description = "API для управления профилем пользователя")
@RequiredArgsConstructor
@RestController
@RequestMapping("/profiles")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    @Operation(summary = "Получить профиль пользователя", description = "Возвращает информацию о текущем профиле пользователя")
    public ResponseEntity<?> getUserProfile() {
        return ok(profileService.getCurrentUser());
    }

    @PostMapping
    @Operation(summary = "Создать профиль пользователя", description = "Создает новый профиль пользователя")
    public ResponseEntity<?> createUserProfile(@RequestBody ProfileRequest request) {
        return ok(profileService.createUserProfile(request));
    }

    @PatchMapping
    @Operation(summary = "Обновить профиль пользователя", description = "Обновляет информацию в профиле пользователя")
    public ResponseEntity<?> updateUserProfile(@RequestBody ProfileRequest request) {
        return ok(profileService.updateUserProfile(request));
    }
}
