package me.leeminsoo.blogdeveloper.controller.view;

import lombok.RequiredArgsConstructor;
import me.leeminsoo.blogdeveloper.domain.Article;
import me.leeminsoo.blogdeveloper.domain.ArticleImage;
import me.leeminsoo.blogdeveloper.domain.Comment;
import me.leeminsoo.blogdeveloper.dto.article.ArticleListViewResponse;
import me.leeminsoo.blogdeveloper.dto.article.ArticleViewResponse;
import me.leeminsoo.blogdeveloper.service.ArticleService;
import me.leeminsoo.blogdeveloper.service.CommentService;
import me.leeminsoo.blogdeveloper.service.ImageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class ArticleViewController {

    private final ArticleService articleService;
    private final CommentService commentService;
    private final ImageService imageService;

    @GetMapping("/articles")
    public String getArticles(@RequestParam(name = "order", defaultValue = "desc") String order,
                              @RequestParam(name = "page", defaultValue = "0") int page,
                              @RequestParam(name = "size", defaultValue = "5") int size,
                              Model model) {
        Page<ArticleListViewResponse> articlePage = articleService.getArticlePage(order, page, size);
        model.addAttribute("articles", articlePage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", articlePage.getTotalPages());
        model.addAttribute("totalItems", articlePage.getTotalElements());
        model.addAttribute("order", order);
        model.addAttribute("size", size);
        return "articleList";
    }

    @GetMapping("/articles/{id}")
    public String getArticle(@PathVariable(name = "id") Long id, @RequestParam(name = "order", defaultValue = "desc") String order, Model model) {
        Article article = articleService.findById(id);
        List<Comment> comments = commentService.getCommentsByArticleIdSorted(id, order);
        List<ArticleImage> images = imageService.getImagesByArticleId(id);
        ArticleViewResponse articleViewResponse = new ArticleViewResponse(article);
        articleViewResponse.setComments(comments);
        articleViewResponse.setImages(images);
        model.addAttribute("article", articleViewResponse);
        return "article";
    }

    @GetMapping("/new-article")
    public String newArticle(@RequestParam(required = false, name = "id") Long id, Model model) {
        if (id == null) {
            model.addAttribute("article", new ArticleViewResponse());
        } else {
            Article article = articleService.findById(id);
            model.addAttribute("article", new ArticleViewResponse(article));
        }
        return "newArticle";
    }

    @GetMapping("/search")
    public String search(@RequestParam(name = "keyword") String keyword, Model model,
                         @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ArticleListViewResponse> searchList = articleService.searchArticle(keyword, pageable);
        model.addAttribute("articles", searchList);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", pageable.getPageNumber());
        model.addAttribute("totalPages", searchList.getTotalPages());
        model.addAttribute("size", pageable.getPageSize());
        return "articleSearch";
    }

    @GetMapping("/")
    public String index(Model model) {
        List<Article> articles = articleService.findRecentArticles();
        List<Comment> comments = commentService.findRecentComments();
        List<ArticleImage> images = imageService.findRecentImages();
        model.addAttribute("articles", articles);
        model.addAttribute("comments", comments);
        model.addAttribute("images", images);
        return "index";
    }
}
