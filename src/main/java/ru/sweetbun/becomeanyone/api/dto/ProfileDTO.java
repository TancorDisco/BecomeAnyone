package ru.sweetbun.becomeanyone.api.dto;

import lombok.Builder;

@Builder
public record ProfileDTO (
        String bio,
        String photoUrl
) {
}
