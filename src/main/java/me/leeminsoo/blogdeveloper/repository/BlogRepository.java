package me.leeminsoo.blogdeveloper.repository;

import me.leeminsoo.blogdeveloper.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlogRepository extends JpaRepository<Article, Long> {
    List<Article> findAllByOrderByCreatedAtDesc();
    List<Article> findAllByOrderByCreatedAtAsc();
    List<Article> findTop10ByOrderByCreatedAtDesc();
}
