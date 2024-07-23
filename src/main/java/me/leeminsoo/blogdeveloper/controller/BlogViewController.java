package me.leeminsoo.blogdeveloper.controller;


import lombok.RequiredArgsConstructor;
import me.leeminsoo.blogdeveloper.domain.Article;
import me.leeminsoo.blogdeveloper.domain.ArticleImage;
import me.leeminsoo.blogdeveloper.domain.Comment;
import me.leeminsoo.blogdeveloper.dto.ArticleListViewResponse;
import me.leeminsoo.blogdeveloper.dto.ArticleViewResponse;
import me.leeminsoo.blogdeveloper.dto.CommentViewResponse;
import me.leeminsoo.blogdeveloper.service.BlogService;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class BlogViewController {

    private final BlogService blogService;

    @GetMapping("favicon.ico")
    @ResponseBody
    public void returnNoFavicon() {
    }

    @GetMapping("/articles")
    public String getArticles(@RequestParam(name = "order",defaultValue = "desc") String order,
            @RequestParam(name = "page",defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
             Model model) {


        Page<ArticleListViewResponse> articlePage  = blogService.getArticlePage(order, page, size);
        model.addAttribute("articles", articlePage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages",articlePage.getTotalPages());
        model.addAttribute("totalItems",articlePage.getTotalElements());
        model.addAttribute("order",order);
        model.addAttribute("size", size);

        return "articleList";
    }
    @GetMapping("/articles/{id}")
    public String getArticle(@PathVariable(name = "id")Long id,@RequestParam(name = "order",defaultValue = "desc") String order, Model model) {

        Article article = blogService.findById(id);
        List<Comment> comments = blogService.getCommentsByArticleIdSorted(id, order);
        List<ArticleImage> images = blogService.getImagesByArticleId(id);

        ArticleViewResponse articleViewResponse = new ArticleViewResponse(article);
        articleViewResponse.setComments(comments);
        articleViewResponse.setImages(images);


        model.addAttribute("article", articleViewResponse);

        return "article";
    }
    @GetMapping("/search")
    public String search(@RequestParam(name = "keyword") String keyword, Model model,
                         @PageableDefault(sort = "id",direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ArticleListViewResponse> searchList = blogService.searchArticle(keyword,pageable);
        model.addAttribute("articles",searchList);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", pageable.getPageNumber());
        model.addAttribute("totalPages", searchList.getTotalPages());
        model.addAttribute("size", pageable.getPageSize());
        return "articleSearch";
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
    @GetMapping("/ImageUpload")
    public String showImageUploadPage(@RequestParam(required = false,name = "id") Long id, Model model) {
        Article article = blogService.findById(id);
        model.addAttribute("article", new ArticleViewResponse(article));
        return "ImageUpload";
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
        List<ArticleImage> images = blogService.findRecentImage();


        model.addAttribute("articles", articles);
        model.addAttribute("comments", comments);
        model.addAttribute("images", images);
        return "index";
    }

    @Value("${file.profileImagePath}")
    private String imageDirPath;

    @GetMapping("/articleImages/{filename}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable("filename") String filename) {
        try {
            Path imageDir = Paths.get(imageDirPath);
            Path file = imageDir.resolve(filename).normalize();
            Resource resource = new FileSystemResource(file.toFile());

            if (resource.exists() && resource.isReadable()) {
                String contentType = Files.probeContentType(file);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.parseMediaType(contentType));
                return new ResponseEntity<>(resource, headers, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }







}

