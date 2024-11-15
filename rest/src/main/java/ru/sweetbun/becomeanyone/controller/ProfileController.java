package ru.sweetbun.becomeanyone.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sweetbun.becomeanyone.dto.profile.ProfileRequest;
import ru.sweetbun.becomeanyone.contract.ProfileService;

import static org.springframework.http.ResponseEntity.ok;


@RequiredArgsConstructor
@RestController
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    public ResponseEntity<?> getUserProfile() {
        return ok(profileService.getCurrentUser());
    }

    @PostMapping
    public ResponseEntity<?> createUserProfile(@RequestBody ProfileRequest profileRequest) {
        return ok(profileService.createUserProfile(profileRequest));
    }

    @PatchMapping
    public ResponseEntity<?> updateUserProfile(@RequestBody ProfileRequest profileRequest) {
        return ok(profileService.updateUserProfile(profileRequest));
    }
}
