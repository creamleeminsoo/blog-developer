package me.leeminsoo.blogdeveloper.dto.comment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import me.leeminsoo.blogdeveloper.domain.Comment;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class CommentViewResponse {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private String author;
    private Long article_id;

    public CommentViewResponse(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt();
        this.author = comment.getAuthor();
        this.article_id = comment.getArticle().getId();
    }
}
