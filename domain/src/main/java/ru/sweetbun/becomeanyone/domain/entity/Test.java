package ru.sweetbun.becomeanyone.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tests")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Test {

    @Id
    @Column(name = "test_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Builder.Default
    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();
}
