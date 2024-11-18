package ru.sweetbun.becomeanyone.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.sweetbun.becomeanyone.contract.ProfileService;
import ru.sweetbun.becomeanyone.dto.profile.ProfileRequest;
import ru.sweetbun.becomeanyone.dto.user.response.UserResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/client/profile")
public class ProfileClientController {

    private final ProfileService userService;

    @GetMapping
    public UserResponse getCurrentUser() {
        return userService.getCurrentUser();
    }

    @PostMapping
    public UserResponse createUserProfile(ProfileRequest profileRequest) {
        return userService.createUserProfile(profileRequest);
    }

    @PatchMapping
    public UserResponse updateUserProfile(ProfileRequest profileRequest) {
        return userService.updateUserProfile(profileRequest);
    }
}
