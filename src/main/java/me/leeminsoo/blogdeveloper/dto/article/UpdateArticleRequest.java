package me.leeminsoo.blogdeveloper.dto.article;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UpdateArticleRequest {
    @NotNull
    private String title;
    @NotNull
    private String content;
}
