package ru.sweetbun.becomeanyone.dto.profile;

import lombok.Builder;

@Builder
public record ProfileRequest(
        String bio,
        String photoUrl
) {
}
