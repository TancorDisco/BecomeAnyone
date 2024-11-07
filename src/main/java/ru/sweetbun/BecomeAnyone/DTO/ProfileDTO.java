package ru.sweetbun.BecomeAnyone.DTO;

import java.util.Set;

public record ProfileDTO (
        String bio,
        String photoUrl,
        Set<SocialLinkDTO> links
) {

    public ProfileDTO {
        if (links == null) links = Set.of();
    }
}
