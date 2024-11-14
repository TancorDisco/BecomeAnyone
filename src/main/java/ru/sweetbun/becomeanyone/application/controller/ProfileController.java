package ru.sweetbun.becomeanyone.application.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sweetbun.becomeanyone.api.dto.ProfileDTO;
import ru.sweetbun.becomeanyone.api.port.ProfileServicePort;

import static org.springframework.http.ResponseEntity.ok;


@RequiredArgsConstructor
@RestController
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileServicePort profileServicePort;

    @GetMapping
    public ResponseEntity<?> getUserProfile() {
        return ok(profileServicePort.getCurrentUser());
    }

    @PostMapping
    public ResponseEntity<?> createUserProfile(@RequestBody ProfileDTO profileDTO) {
        return ok(profileServicePort.createUserProfile(profileDTO));
    }

    @PatchMapping
    public ResponseEntity<?> updateUserProfile(@RequestBody ProfileDTO profileDTO) {
        return ok(profileServicePort.updateUserProfile(profileDTO));
    }
}
