package me.leeminsoo.blogdeveloper.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import me.leeminsoo.blogdeveloper.config.error.exception.ArticleNotFoundException;
import me.leeminsoo.blogdeveloper.domain.Article;
import me.leeminsoo.blogdeveloper.domain.ArticleImage;
import me.leeminsoo.blogdeveloper.dto.article.AddArticleRequest;
import me.leeminsoo.blogdeveloper.dto.article.ArticleListViewResponse;
import me.leeminsoo.blogdeveloper.dto.article.UpdateArticleRequest;
import me.leeminsoo.blogdeveloper.repository.ArticleRepository;
import me.leeminsoo.blogdeveloper.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final ImageRepository imageRepository;

    @Value("${file.profileImagePath}")
    private String uploadFolder;

    public Article save(AddArticleRequest request, String userName) {
        return articleRepository.save(request.toEntity(userName));
    }

    public List<Article> findAll() {
        return articleRepository.findAll();
    }

    public Article findById(long id) {
        return articleRepository.findById(id)
                .orElseThrow(ArticleNotFoundException::new);
    }

    @Transactional
    public void delete(long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found : " + id));
        authorizeArticleAuthor(article);
        List<ArticleImage> images = imageRepository.findByArticleId(id);

        for (ArticleImage image : images) {
            String imagePath = image.getUrl();
            String fileName = imagePath.substring(imagePath.lastIndexOf('/') + 1);
            Path filePath = Paths.get(uploadFolder, fileName);

            File file = filePath.toFile();
            if (file.exists()) {
                file.delete();
            }
        }
        articleRepository.delete(article);
    }

    @Transactional
    public Article update(long id, UpdateArticleRequest request) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found : " + id));
        authorizeArticleAuthor(article);
        article.update(request.getTitle(), request.getContent());
        return article;
    }

    public Page<ArticleListViewResponse> getArticlePage(String order, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.fromString(order), "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Article> articlePage = articleRepository.findAll(pageable);
        return articlePage.map(ArticleListViewResponse::new);
    }

    public List<Article> findRecentArticles() {
        return articleRepository.findTop10ByOrderByCreatedAtDesc();
    }

    private static void authorizeArticleAuthor(Article article) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!article.getAuthor().equals(userName)) {
            throw new IllegalArgumentException("권한이 없습니다");
        }
    }

    public void saveArticleImages(List<MultipartFile> images, Long articleId) {
        Article article = articleRepository.findById(articleId).orElseThrow(ArticleNotFoundException::new);
        authorizeArticleAuthor(article);
        if (images != null && !images.isEmpty()) {
            for (MultipartFile file : images) {
                UUID uuid = UUID.randomUUID();
                String imageFileName = uuid + "_" + file.getOriginalFilename();
                File destinationFile = new File(uploadFolder + imageFileName);

                try {
                    file.transferTo(destinationFile);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                ArticleImage articleImage = ArticleImage.builder()
                        .url("/articleImages/" + imageFileName)
                        .article(article)
                        .build();

                imageRepository.save(articleImage);
            }
        }
    }

    public List<ArticleImage> getImagesByArticleId(Long id) {
        return imageRepository.findByArticleId(id);
    }

    @Transactional
    public Page<ArticleListViewResponse> searchArticle(String keyword,Pageable pageable) {
        Page<Article> searchPage =  articleRepository.findByTitleContaining(keyword,pageable);
        return searchPage.map(ArticleListViewResponse::new);
    }
}
