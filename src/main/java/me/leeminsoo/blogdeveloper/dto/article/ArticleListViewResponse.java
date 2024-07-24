package me.leeminsoo.blogdeveloper.dto.article;

import lombok.Getter;
import me.leeminsoo.blogdeveloper.domain.Article;

@Getter
public class ArticleListViewResponse {
    private final Long id;
    private final String title;
    private final String content;
    private final String author;

    public ArticleListViewResponse(Article article) {
        this.id = article.getId();
        this.title = article.getTitle();
        this.content = article.getContent();
        this.author = article.getAuthor();
    }

}
