package me.leeminsoo.blogdeveloper.dto.article;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.leeminsoo.blogdeveloper.domain.Article;


@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddArticleRequest {
    @NotNull
    @Size(min =1, max = 20)
    private String title;

    @NotNull
    private String content;

    public Article toEntity(String author) {
        return Article.builder()
                .title(title)
                .content(content)
                .author(author)
                .build();
    }
}