package me.leeminsoo.blogdeveloper.repository;

import jakarta.transaction.Transactional;
import me.leeminsoo.blogdeveloper.domain.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findTop10ByOrderByCreatedAtDesc();
    Page<Article> findAll(Pageable pageable);
    Page<Article> findByTitleContaining(String keyword, Pageable pageable);

    @Modifying
    @Transactional
    @Query("update Article p set p.view = p.view + 1 where p.id = :id")
    int updateView(@Param("id") Long id);
}
