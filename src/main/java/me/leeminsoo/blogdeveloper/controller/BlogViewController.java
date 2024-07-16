package me.leeminsoo.blogdeveloper.controller;

import lombok.RequiredArgsConstructor;
import me.leeminsoo.blogdeveloper.domain.Article;
import me.leeminsoo.blogdeveloper.domain.Comment;
import me.leeminsoo.blogdeveloper.domain.User;
import me.leeminsoo.blogdeveloper.dto.ArticleListViewResponse;
import me.leeminsoo.blogdeveloper.dto.ArticleViewResponse;
import me.leeminsoo.blogdeveloper.dto.CommentViewResponse;
import me.leeminsoo.blogdeveloper.service.BlogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Controller
public class BlogViewController {

    private final BlogService blogService;

    @GetMapping("/articles")
    public String getArticles(@RequestParam(name = "order",defaultValue = "desc") String order, Model model) {
        List<ArticleListViewResponse> articles = blogService.getArticleSorted(order).stream()
                .map(ArticleListViewResponse::new)
                .toList();
        model.addAttribute("articles", articles);

        return "articleList";
    }
    @GetMapping("/articles/{id}")
    public String getArticle(@PathVariable(name = "id")Long id,@RequestParam(name = "order",defaultValue = "desc") String order, Model model) {

        Article article = blogService.findById(id);
        List<Comment> comments = blogService.getCommentsByArticleIdSorted(id, order);
        ArticleViewResponse articleViewResponse = new ArticleViewResponse(article);
        articleViewResponse.setComments(comments);

        model.addAttribute("article", articleViewResponse);

        return "article";
    }
    @GetMapping("/new-article")
    public String newArticle(@RequestParam(required = false,name = "id") Long id, Model model){
        if(id == null) {
            model.addAttribute("article", new ArticleViewResponse());
        } else {
            Article article = blogService.findById(id);
            model.addAttribute("article", new ArticleViewResponse(article));
        }
        return "newArticle";
    }

    @GetMapping("/new-comment")
    public String newComment(@RequestParam(required = false,name = "id") Long id, Model model){
        if(id == null) {
            model.addAttribute("comment", new CommentViewResponse());
        } else {
            Comment comment = blogService.findByCommentId(id);
            model.addAttribute("comment", new CommentViewResponse(comment));
        }
        return "newComment";
    }

    @GetMapping("/")
    public String index(Model model) {
        List<Article> articles = blogService.findRecentArticles();
        List<Comment> comments = blogService.findRecentComments();


        model.addAttribute("articles", articles);
        model.addAttribute("comments", comments);
        return "index";
    }




}

