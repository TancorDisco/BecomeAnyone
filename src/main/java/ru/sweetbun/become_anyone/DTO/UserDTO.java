package ru.sweetbun.become_anyone.DTO;

import lombok.Builder;

@Builder
public record UserDTO (
        String username,
        String email,
        String password
) {
}
