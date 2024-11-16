package ru.sweetbun.becomeanyone.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "test_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestResult {

    @Id
    @Column(name = "test_result_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "progress_id")
    private Progress progress;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "test_id")
    private Test test;

    @Column(nullable = false)
    private double percent;
}
