package ru.sweetbun.becomeanyone.api.dto;

import lombok.Builder;

@Builder
public record UserDTO (
        String username,
        String email,
        String password
) {
}
