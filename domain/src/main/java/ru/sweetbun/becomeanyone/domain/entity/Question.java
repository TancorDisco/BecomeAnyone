package ru.sweetbun.becomeanyone.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Question {

    @Id
    @Column(name = "question_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "test_id")
    private Test test;

    @Column(nullable = false)
    private String questionText;

    @Builder.Default
    @Column(nullable = false)
    private boolean hasSeveralCorrectAnswers = false;

    @Column
    private String explanation;

    @Column
    private String imageUrl;

    @Builder.Default
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private List<Answer> answers = new ArrayList<>();
}
