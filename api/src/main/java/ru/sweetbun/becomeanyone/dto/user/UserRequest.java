package ru.sweetbun.becomeanyone.dto.user;

import lombok.Builder;

@Builder
public record UserRequest(
        String username,
        String email,
        String password
) {
}
