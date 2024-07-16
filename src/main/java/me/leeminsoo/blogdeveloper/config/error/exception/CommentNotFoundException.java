package me.leeminsoo.blogdeveloper.config.error.exception;

import me.leeminsoo.blogdeveloper.config.error.ErrorCode;

public class CommentNotFoundException extends NotFoundException{
    public CommentNotFoundException() {
        super(ErrorCode.COMMENT_NOT_FOUND);
    }
}
