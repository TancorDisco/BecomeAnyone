package ru.sweetbun.becomeanyone.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.sweetbun.becomeanyone.dto.profile.ProfileRequest;
import ru.sweetbun.becomeanyone.dto.user.response.UserResponse;

@FeignClient(name = "profileService", url = "http://localhost:8080")
public interface ProfileServiceClient {

    @PostMapping("/profiles")
    UserResponse createUserProfile(@RequestBody ProfileRequest profileRequest);

    @PatchMapping("/profiles")
    UserResponse updateUserProfile(@RequestBody ProfileRequest profileRequest);

    @GetMapping("/profiles")
    UserResponse getCurrentUser();
}
