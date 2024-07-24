package me.leeminsoo.blogdeveloper.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.leeminsoo.blogdeveloper.domain.Comment;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddCommentResponse {
    private Long id;
    private String content;

    public AddCommentResponse(Comment comment) {
        this.id=comment.getId();
        this.content =comment.getContent();
    }
}
