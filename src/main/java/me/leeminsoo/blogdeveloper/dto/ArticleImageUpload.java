package me.leeminsoo.blogdeveloper.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ArticleImageUpload {
    @NotNull
    @Size(min = 1, message = "최소 하나 이상의 이미지를 업로드해야 합니다.")
    private List<MultipartFile> images;
}
