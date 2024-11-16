package ru.sweetbun.becomeanyone.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "modules")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Module {

    @Id
    @Column(name = "module_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id")
    private Course course;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "order_num")
    private int orderNum;

    @Builder.Default
    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL)
    private List<Lesson> lessons = new ArrayList<>();
}
