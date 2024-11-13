package ru.sweetbun.becomeanyone.DTO;

import lombok.Builder;

@Builder
public record UserDTO (
        String username,
        String email,
        String password
) {
}
