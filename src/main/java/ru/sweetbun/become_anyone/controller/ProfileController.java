package ru.sweetbun.become_anyone.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sweetbun.become_anyone.DTO.ProfileDTO;
import ru.sweetbun.become_anyone.service.UserService;

import static org.springframework.http.ResponseEntity.ok;


@RequiredArgsConstructor
@RestController
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<?> getUserProfile() {
        return ok(userService.getCurrentUser());
    }

    @PostMapping
    public ResponseEntity<?> createUserProfile(@RequestBody ProfileDTO profileDTO) {
        return ok(userService.createUserProfile(profileDTO));
    }

    @PatchMapping
    public ResponseEntity<?> updateUserProfile(@RequestBody ProfileDTO profileDTO) {
        return ok(userService.updateUserProfile(profileDTO));
    }
}