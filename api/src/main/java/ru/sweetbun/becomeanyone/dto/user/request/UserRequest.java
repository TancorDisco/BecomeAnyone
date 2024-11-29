package ru.sweetbun.becomeanyone.dto.user.request;

import lombok.Builder;

@Builder
public record UserRequest(
        String username,
        String email,
        String password
) {
}
