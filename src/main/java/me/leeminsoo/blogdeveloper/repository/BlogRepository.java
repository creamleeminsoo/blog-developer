package me.leeminsoo.blogdeveloper.repository;

import me.leeminsoo.blogdeveloper.domain.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlogRepository extends JpaRepository<Article, Long> {
    List<Article> findTop10ByOrderByCreatedAtDesc();
    Page<Article> findAll(Pageable pageable);
    Page<Article> findByTitleContaining(String keyword, Pageable pageable);
}
