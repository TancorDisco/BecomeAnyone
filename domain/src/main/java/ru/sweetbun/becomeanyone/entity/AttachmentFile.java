package ru.sweetbun.becomeanyone.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "attachment_files")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentFile {

    @Id
    @Column(name = "file_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "content_id")
    private Content content;

    @Column(nullable = false)
    private String key;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false)
    private String contentType;

    @Column(nullable = false)
    private Long size;
}
