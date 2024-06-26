package com.hanghae.theham.global.exception;

import com.hanghae.theham.global.dto.ResponseDto;
import com.hanghae.theham.global.slack.SlackNotification;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler {

    @SlackNotification
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public ResponseDto<?> handleBadRequestException(HttpServletRequest request, BadRequestException e) {
        log.error("handleBadRequestException", e);
        return ResponseDto.fail(e.getMessage());
    }

    @SlackNotification
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(TokenException.class)
    public ResponseDto<?> handleTokenException(HttpServletRequest request, TokenException e) {
        log.error("handleTokenException", e);
        return ResponseDto.fail(e.getMessage());
    }

    @SlackNotification
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(AwsS3Exception.class)
    public ResponseDto<?> handleAwsS3Exception(HttpServletRequest request, AwsS3Exception e) {
        log.error("handleAwsS3Exception", e);
        return ResponseDto.fail(e.getMessage());
    }

    @SlackNotification
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseDto<?> handleMethodArgumentNotValidException(HttpServletRequest request, MethodArgumentNotValidException e) {
        Map<String, String> errorMap = new HashMap<>();
        BindingResult result = e.getBindingResult();

        for (FieldError error : result.getFieldErrors()) {
            errorMap.put(error.getField(), error.getDefaultMessage());
        }

        log.error("handleMethodArgumentNotValidException", e);
        return ResponseDto.fail("유효성 검사 실패", errorMap);
    }

    @SlackNotification
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseDto<?> handleMethodArgumentTypeMismatchException(HttpServletRequest request, MethodArgumentTypeMismatchException e) {
        log.error("handleMethodArgumentTypeMismatchException", e);
        return ResponseDto.fail(e.getMessage());
    }

    @SlackNotification
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseDto<?> handleHttpRequestMethodNotSupportedException(HttpServletRequest request, HttpRequestMethodNotSupportedException e) {
        log.error("handleHttpRequestMethodNotSupportedException", e);
        return ResponseDto.fail(e.getMessage());
    }

    @SlackNotification
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseDto<?> handleAccessDeniedException(HttpServletRequest request, AccessDeniedException e) {
        log.error("handleAccessDeniedException", e);
        return ResponseDto.fail(e.getMessage());
    }

    @SlackNotification
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingRequestCookieException.class)
    public ResponseDto<?> handleMissingRequestCookieException(HttpServletRequest request, MissingRequestCookieException e) {
        log.error("handleMissingRequestCookieException", e);
        return ResponseDto.fail(e.getMessage());
    }

    @SlackNotification
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ResponseDto<?> handleException(HttpServletRequest request, Exception e) {
        log.error("handleException", e);
        return ResponseDto.fail(ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
    }
}
