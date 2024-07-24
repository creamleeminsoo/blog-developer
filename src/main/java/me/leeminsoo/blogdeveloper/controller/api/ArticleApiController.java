package me.leeminsoo.blogdeveloper.controller.api;

import lombok.RequiredArgsConstructor;
import me.leeminsoo.blogdeveloper.domain.Article;
import me.leeminsoo.blogdeveloper.dto.article.AddArticleRequest;
import me.leeminsoo.blogdeveloper.dto.article.UpdateArticleRequest;
import me.leeminsoo.blogdeveloper.service.ArticleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RequiredArgsConstructor
@RestController
public class ArticleApiController {

    private final ArticleService articleService;

    @PostMapping("/api/articles")
    public ResponseEntity<Article> addArticle(@RequestBody @Validated AddArticleRequest request, Principal principal) {
        Article savedArticle = articleService.save(request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedArticle);
    }

    @DeleteMapping("/api/articles/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable(name = "id") long id) {
        articleService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/api/articles/{id}")
    public ResponseEntity<Article> updateArticle(@PathVariable(name = "id") long id, @RequestBody @Validated UpdateArticleRequest request) {
        Article updatedArticle = articleService.update(id, request);
        return ResponseEntity.ok().body(updatedArticle);
    }
}
