package ru.sweetbun.become_anyone.DTO;

import lombok.Builder;

@Builder
public record ProfileDTO (
        String bio,
        String photoUrl
) {
}
