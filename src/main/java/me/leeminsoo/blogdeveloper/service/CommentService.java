package me.leeminsoo.blogdeveloper.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import me.leeminsoo.blogdeveloper.config.error.exception.CommentNotFoundException;
import me.leeminsoo.blogdeveloper.domain.Article;
import me.leeminsoo.blogdeveloper.domain.Comment;
import me.leeminsoo.blogdeveloper.dto.comment.AddCommentRequest;
import me.leeminsoo.blogdeveloper.dto.comment.UpdateCommentRequest;
import me.leeminsoo.blogdeveloper.repository.ArticleRepository;
import me.leeminsoo.blogdeveloper.repository.CommentRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;

    public Comment findByCommentId(long id) {
        return commentRepository.findById(id)
                .orElseThrow(CommentNotFoundException::new);
    }

    @Transactional
    public Comment updateComment(Long id, UpdateCommentRequest request) {
        Comment comment = commentRepository.findById(id).orElseThrow(CommentNotFoundException::new);
        authorizeCommentAuthor(comment);
        comment.update(request.getContent());
        return comment;
    }

    @Transactional
    public void deleteComment(Long id) {
        Comment comment = commentRepository.findById(id).orElseThrow(CommentNotFoundException::new);
        authorizeCommentAuthor(comment);
        commentRepository.deleteById(id);
    }

    public Comment addComment(AddCommentRequest request, String userName) {
        Article article = articleRepository.findById(request.getArticleId())
                .orElseThrow(() -> new IllegalArgumentException("not found: " + request.getArticleId()));
        return commentRepository.save(request.toEntity(userName, article));
    }

    public List<Comment> getCommentsByArticleIdSorted(long articleId, String order) {
        if (order.equals("desc")) {
            return commentRepository.findByArticleIdOrderByCreatedAtDesc(articleId);
        } else {
            return commentRepository.findByArticleIdOrderByCreatedAtAsc(articleId);
        }
    }

    public List<Comment> findRecentComments() {
        return commentRepository.findTop10ByOrderByCreatedAtDesc();
    }

    private static void authorizeCommentAuthor(Comment comment) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!comment.getAuthor().equals(userName)) {
            throw new IllegalArgumentException("권한이 없습니다");
        }
    }
}
