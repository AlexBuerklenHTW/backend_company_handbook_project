package com.example.unternehmenshandbuch.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "articles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Article {

    public enum ArticleStatus {
        EDITING,
        SUBMITTED,
        APPROVED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String publicId;

    @Column(nullable = false, length = 200)
    private String description;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column()
    private Integer version;

    @Column()
    @Enumerated(EnumType.STRING)
    private ArticleStatus status;

    @Column()
    private String editedBy;

    @Column
    private Boolean isEditable;

    @Column
    private Boolean isSubmitted;

    @Column()
    private String denyText;

    @CreationTimestamp
    private Instant createdAt;
}
