package me.leeminsoo.blogdeveloper.repository;

import me.leeminsoo.blogdeveloper.domain.ArticleImage;
import me.leeminsoo.blogdeveloper.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<ArticleImage, Long> {
    List<ArticleImage> findByArticleId(Long articleId);
    List<ArticleImage> findTop10ByOrderById();
}
