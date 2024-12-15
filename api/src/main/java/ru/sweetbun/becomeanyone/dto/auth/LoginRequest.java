package ru.sweetbun.becomeanyone.dto.auth;

import lombok.Builder;

@Builder
public record LoginRequest(
        String username,
        String password
) {
}
