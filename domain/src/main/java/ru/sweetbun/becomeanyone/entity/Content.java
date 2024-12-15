package ru.sweetbun.becomeanyone.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "contents")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Content {

    @Id
    @Column(name = "content_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    @Column
    private String text;

    @Builder.Default
    @OneToOne(mappedBy = "content", cascade = CascadeType.ALL)
    private Video video = null;

    @Builder.Default
    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL)
    private List<AttachmentFile> files = new ArrayList<>();
}
