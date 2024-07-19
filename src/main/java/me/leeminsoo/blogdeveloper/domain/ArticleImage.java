package me.leeminsoo.blogdeveloper.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "articleImage")
public class ArticleImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String url;

    @ManyToOne
    @JoinColumn(name = "article_Id")
    private Article article;

    public String getFilename() {
        return url.substring(url.lastIndexOf("/") + 1);
    }
}
