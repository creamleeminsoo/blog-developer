package me.leeminsoo.blogdeveloper.controller.api;

import lombok.RequiredArgsConstructor;
import me.leeminsoo.blogdeveloper.dto.image.ArticleImageUpload;
import me.leeminsoo.blogdeveloper.service.ArticleService;
import me.leeminsoo.blogdeveloper.service.ImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ImageApiController {

    private final ArticleService articleService;


    @PostMapping("/api/articles/image")
    public ResponseEntity<String> addArticleImage(@ModelAttribute @Validated ArticleImageUpload imageUpload, @RequestParam("articleId") Long articleId) {
        List<MultipartFile> images = imageUpload.getImages();
        for (MultipartFile image : images) {
            if (image.isEmpty()) {
                return ResponseEntity.badRequest().body("빈 파일은 업로드할 수 없습니다.");
            }
        }
        articleService.saveArticleImages(images, articleId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
