package me.leeminsoo.blogdeveloper.config.error;

import lombok.extern.slf4j.Slf4j;
import me.leeminsoo.blogdeveloper.config.error.exception.BusinessBaseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice //컨트롤러에서 예외발생하면 핸들러가 예외처리해줌
public class GlobalExceptionHandler {
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ErrorResponse> handle(HttpRequestMethodNotSupportedException e){ //컨트롤러에서 메서드 못찾을경우 예외처리 해주는 메서드
        log.error("HttpRequestMethodNotSupportedException", e);
        return createErrorResponseEntity(ErrorCode.METHOD_NOT_ALLOWED);
    }
    @ExceptionHandler(BusinessBaseException.class)
    protected ResponseEntity<ErrorResponse> handle(BusinessBaseException e) {
        log.error("BusinessException",e);
        return createErrorResponseEntity(e.getErrorCode());
    }
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handle(Exception e){
        e.printStackTrace();
        log.error("Exception",e);
        return createErrorResponseEntity(ErrorCode.INTERNAL_SERVER_ERROR);
    }
    private ResponseEntity<ErrorResponse> createErrorResponseEntity(ErrorCode errorCode){
        return new ResponseEntity<>(
            ErrorResponse.of(errorCode),
            errorCode.getStatus()
        );


    }
}
