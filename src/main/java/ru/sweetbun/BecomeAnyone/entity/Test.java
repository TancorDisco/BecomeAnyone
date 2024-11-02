package ru.sweetbun.BecomeAnyone.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tests")
@Data
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

    @Column(name = "title")
    @NotEmpty
    private String title;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();
}
