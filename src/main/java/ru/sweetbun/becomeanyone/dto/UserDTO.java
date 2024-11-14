package ru.sweetbun.becomeanyone.dto;

import lombok.Builder;

@Builder
public record UserDTO (
        String username,
        String email,
        String password
) {
}
