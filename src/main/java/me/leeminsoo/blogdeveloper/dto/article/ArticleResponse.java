package me.leeminsoo.blogdeveloper.dto.article;

import lombok.Getter;
import me.leeminsoo.blogdeveloper.domain.Article;
import me.leeminsoo.blogdeveloper.domain.ArticleImage;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ArticleResponse {

    private final String title;
    private final String content;
    private List<String> imageUrls;

    public ArticleResponse(Article article){
        this.title = article.getTitle();
        this.content = article.getContent();

    }
    public ArticleResponse(Article article,List<String> imageUrls){
        this.title = article.getTitle();
        this.content = article.getContent();
        this.imageUrls = article.getArticleImages().stream()
                .map(ArticleImage::getUrl)
                .collect(Collectors.toList());

    }

}
