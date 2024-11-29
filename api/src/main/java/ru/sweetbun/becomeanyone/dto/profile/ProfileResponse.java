package ru.sweetbun.becomeanyone.dto.profile;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {
    private Long id;
    private String bio;
    private String photoUrl;
}
