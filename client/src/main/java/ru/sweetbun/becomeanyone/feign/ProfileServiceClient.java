package ru.sweetbun.becomeanyone.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.sweetbun.becomeanyone.dto.profile.ProfileRequest;
import ru.sweetbun.becomeanyone.dto.user.response.UserResponse;

@RequestMapping("/profile")
@FeignClient(name = "profileService", url = "http://localhost:8080")
public interface ProfileServiceClient {

    @PostMapping
    UserResponse createUserProfile(@RequestBody ProfileRequest profileRequest);

    @PatchMapping
    UserResponse updateUserProfile(@RequestBody ProfileRequest profileRequest);

    @GetMapping
    UserResponse getCurrentUser();
}
