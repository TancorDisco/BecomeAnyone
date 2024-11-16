package ru.sweetbun.becomeanyone.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "social_links")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SocialLink {

    @Id
    @Column(name = "link_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @Size(max = 50)
    @Column(name = "platform", nullable = false)
    private String platform;

    @Column(name = "url", nullable = false)
    private String url;
}
