package ru.sweetbun.BecomeAnyone.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class ProfileDTO {

    private String bio;
    private String photoUrl;
    private Set<SocialLinkDTO> links;
}
