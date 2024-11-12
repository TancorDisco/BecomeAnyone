package ru.sweetbun.becomeanyone.DTO;

import lombok.Builder;

@Builder
public record ProfileDTO (
        String bio,
        String photoUrl
) {
}
