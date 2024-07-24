package me.leeminsoo.blogdeveloper.controller.view;

import lombok.RequiredArgsConstructor;
import me.leeminsoo.blogdeveloper.domain.Comment;
import me.leeminsoo.blogdeveloper.dto.comment.CommentViewResponse;
import me.leeminsoo.blogdeveloper.service.CommentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
public class CommentViewController {

    private final CommentService commentService;

    @GetMapping("/new-comment")
    public String newComment(@RequestParam(required = false, name = "commentId") Long commentId, Model model) {
        if (commentId == null) {
            model.addAttribute("comment", new CommentViewResponse());
        } else {
            Comment comment = commentService.findByCommentId(commentId);
            model.addAttribute("comment", new CommentViewResponse(comment));
        }
        return "newComment";
    }
}
