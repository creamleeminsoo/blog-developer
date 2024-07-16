package me.leeminsoo.blogdeveloper.repository;

import me.leeminsoo.blogdeveloper.domain.Comment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByArticleIdOrderByCreatedAtDesc(Long articleId);
    List<Comment> findByArticleIdOrderByCreatedAtAsc(Long articleId);
    List<Comment> findTop10ByOrderByCreatedAtDesc();





}
