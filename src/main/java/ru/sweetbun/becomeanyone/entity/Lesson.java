package ru.sweetbun.becomeanyone.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lessons")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Lesson {

    @Id
    @Column(name = "lesson_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "module_id")
    private Module module;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "order_num")
    private int orderNum;

    @Builder.Default
    @JsonManagedReference
    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Test> tests = new ArrayList<>();
}
