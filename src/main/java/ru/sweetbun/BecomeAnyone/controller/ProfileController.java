package ru.sweetbun.BecomeAnyone.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sweetbun.BecomeAnyone.DTO.ProfileDTO;
import ru.sweetbun.BecomeAnyone.service.UserService;

@RestController
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;

    @Autowired
    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<?> getUserProfile() {
        return ResponseEntity.ok(userService.getUserProfile());
    }

    @PostMapping
    public ResponseEntity<?> createUserProfile(@RequestBody ProfileDTO profileDTO) {
        return ResponseEntity.ok(userService.createUserProfile(profileDTO));
    }

    @PutMapping
    public ResponseEntity<?> updateUserProfile(@RequestBody ProfileDTO profileDTO) {
        return ResponseEntity.ok(userService.updateUserProfile(profileDTO));
    }
}
