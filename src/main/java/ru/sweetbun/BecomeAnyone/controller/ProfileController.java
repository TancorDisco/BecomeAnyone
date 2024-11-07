package ru.sweetbun.BecomeAnyone.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sweetbun.BecomeAnyone.DTO.ProfileDTO;
import ru.sweetbun.BecomeAnyone.service.UserService;


@RequiredArgsConstructor
@RestController
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<?> getUserProfile() {
        return ResponseEntity.ok(userService.getUserProfile());
    }

    @PostMapping
    public ResponseEntity<?> createUserProfile(@RequestBody ProfileDTO profileDTO) {
        return ResponseEntity.ok(userService.createUserProfile(profileDTO));
    }

    @PatchMapping
    public ResponseEntity<?> updateUserProfile(@RequestBody ProfileDTO profileDTO) {
        return ResponseEntity.ok(userService.updateUserProfile(profileDTO));
    }
}
