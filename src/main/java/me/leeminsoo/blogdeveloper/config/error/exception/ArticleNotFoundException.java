package me.leeminsoo.blogdeveloper.config.error.exception;

import me.leeminsoo.blogdeveloper.config.error.ErrorCode;

public class ArticleNotFoundException extends NotFoundException{
    public ArticleNotFoundException() {
        super(ErrorCode.ARTICLE_NOT_FOUND);
    }
}
