package me.leeminsoo.blogdeveloper.controller.view;

import lombok.RequiredArgsConstructor;
import me.leeminsoo.blogdeveloper.domain.Article;
import me.leeminsoo.blogdeveloper.dto.article.ArticleViewResponse;
import me.leeminsoo.blogdeveloper.service.ArticleService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
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

@RequiredArgsConstructor
@Controller
public class ImageViewController {

    private final ArticleService articleService;

    @Value("${file.profileImagePath}")
    private String imageDirPath;

    @GetMapping("/ImageUpload")
    public String showImageUploadPage(@RequestParam(required = false, name = "id") Long id, Model model) {
        Article article = articleService.findById(id);
        model.addAttribute("article", new ArticleViewResponse(article));
        return "ImageUpload";
    }

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
