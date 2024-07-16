package me.leeminsoo.blogdeveloper.repository;

import me.leeminsoo.blogdeveloper.domain.ArticleImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<ArticleImage, Long> {
}
