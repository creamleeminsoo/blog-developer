package me.leeminsoo.blogdeveloper.controller.api;

import lombok.RequiredArgsConstructor;
import me.leeminsoo.blogdeveloper.domain.Comment;
import me.leeminsoo.blogdeveloper.dto.comment.AddCommentRequest;
import me.leeminsoo.blogdeveloper.dto.comment.AddCommentResponse;
import me.leeminsoo.blogdeveloper.dto.comment.UpdateCommentRequest;
import me.leeminsoo.blogdeveloper.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RequiredArgsConstructor
@RestController

public class CommentApiController {

    private final CommentService commentService;

    @PostMapping("/api/comments")
    public ResponseEntity<AddCommentResponse> addComment(@RequestBody @Validated AddCommentRequest request, Principal principal) {
        Comment savedComment = commentService.addComment(request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AddCommentResponse(savedComment));
    }

    @PutMapping("/api/comments/{commentId}")
    public ResponseEntity<Comment> updateComment(@PathVariable(name = "commentId") Long commentId,
                                                 @RequestBody @Validated UpdateCommentRequest request) {
        Comment updatedComment = commentService.updateComment(commentId, request);
        return ResponseEntity.ok().body(updatedComment);
    }

    @DeleteMapping("/api/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable(name = "commentId") Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok().build();
    }
}
