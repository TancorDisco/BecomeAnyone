package ru.sweetbun.becomeanyone.dto;

import lombok.Builder;

@Builder
public record ProfileDTO (
        String bio,
        String photoUrl
) {
}
