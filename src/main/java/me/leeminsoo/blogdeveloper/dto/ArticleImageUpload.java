package me.leeminsoo.blogdeveloper.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ArticleImageUpload {
    private List<MultipartFile> images;
}
