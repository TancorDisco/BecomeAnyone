package ru.sweetbun.BecomeAnyone.DTO;

import lombok.Builder;

@Builder
public record ProfileDTO (
        String bio,
        String photoUrl
) {
}
