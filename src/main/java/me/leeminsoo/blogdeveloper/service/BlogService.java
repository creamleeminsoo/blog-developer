package me.leeminsoo.blogdeveloper.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import me.leeminsoo.blogdeveloper.config.error.exception.ArticleNotFoundException;
import me.leeminsoo.blogdeveloper.config.error.exception.CommentNotFoundException;
import me.leeminsoo.blogdeveloper.domain.Article;
import me.leeminsoo.blogdeveloper.domain.ArticleImage;
import me.leeminsoo.blogdeveloper.domain.Comment;
import me.leeminsoo.blogdeveloper.dto.*;
import me.leeminsoo.blogdeveloper.repository.BlogRepository;
import me.leeminsoo.blogdeveloper.repository.CommentRepository;
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
public class BlogService {

    private final BlogRepository blogRepository;
    private final CommentRepository commentRepository;
    private final ImageRepository imageRepository;

    @Value("${file.profileImagePath}")
    private String uploadFolder;



    public Article save(AddArticleRequest request, String userName) {
        return blogRepository.save(request.toEntity(userName));
    }

    public List<Article> findAll() {
        return blogRepository.findAll();
    }

    public Article findById(long id) {
        return blogRepository.findById(id)
                .orElseThrow(ArticleNotFoundException::new);
    }

    public void delete(long id) {
        Article article = blogRepository.findById(id)
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
        blogRepository.delete(article);
    }

    public Comment findByCommentId(long id) {
        return commentRepository.findById(id)
                .orElseThrow(CommentNotFoundException::new);
    }

    @Transactional
    public Article update(long id, UpdateArticleRequest request) {
        Article article = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found : " + id));

        authorizeArticleAuthor(article);
        article.update(request.getTitle(), request.getContent());

        return article;
    }

    private static void authorizeArticleAuthor(Article article) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!article.getAuthor().equals(userName)) {
            throw new IllegalArgumentException("권한이 없습니다");
        }
    }

    public Comment addComment(AddCommentRequest request, String userName) {
        Article article = blogRepository.findById(request.getArticleId())
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

    public Page<ArticleListViewResponse> getArticlePage(String order, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.fromString(order),"id");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Article> articlePage = blogRepository.findAll(pageable);
        return articlePage.map(ArticleListViewResponse::new);
    }


    public void comment_delete(Long id) {
        Comment comment = commentRepository.findById(id).orElseThrow(CommentNotFoundException::new);

        authorizeArticleAuthor(comment);
        commentRepository.deleteById(id);
    }

    @Transactional
    public Comment comment_update(Long id, UpdateCommentRequest request) {
        Comment comment = commentRepository.findById(id).orElseThrow(CommentNotFoundException::new);

        authorizeArticleAuthor(comment);
        comment.update(request.getContent());
        return comment;
    }


    private static void authorizeArticleAuthor(Comment comment) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!comment.getAuthor().equals(userName)) {
            throw new IllegalArgumentException("권한이 없습니다");
        }
    }


    public List<Article> findRecentArticles() {
        return blogRepository.findTop10ByOrderByCreatedAtDesc();
    }

    public List<Comment> findRecentComments() {
        return commentRepository.findTop10ByOrderByCreatedAtDesc();
    }



    public void saveArticleImages(List<MultipartFile> images,Long articleId) {
        Article article = blogRepository.findById(articleId).orElseThrow(ArticleNotFoundException::new);
        authorizeArticleAuthor(article);
        if (images != null && !images.isEmpty()){
            for(MultipartFile file : images) {
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

    public List<ArticleImage> findRecentImage(){
        return imageRepository.findTop10ByOrderById();
    }

    @Transactional
    public Page<ArticleListViewResponse> searchArticle(String keyword,Pageable pageable) {
        Page<Article> searchPage =  blogRepository.findByTitleContaining(keyword,pageable);
        return searchPage.map(ArticleListViewResponse::new);
    }


}