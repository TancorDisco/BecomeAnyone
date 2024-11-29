package ru.sweetbun.becomeanyone.dto.auth;

public record LoginRequest(
        String username,
        String password
) {
}
