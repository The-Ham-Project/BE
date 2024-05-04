package com.hanghae.theham.global.websocket.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.hanghae.theham.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class MessageErrorResponse<T> {
    private final boolean status;
    private final String code;
    private final String message;
    @JsonInclude(Include.NON_EMPTY)
    private final T data;

    public MessageErrorResponse(boolean status, ErrorCode errorCode, T data) {
        this.status = status;
        this.code = errorCode.name();
        this.message = errorCode.getMessage();
        this.data = data;
    }

    public static <T> MessageErrorResponse<T> response(ErrorCode errorCode, T data) {
        return new MessageErrorResponse<>(false, errorCode, data);
    }

    public static <T> MessageErrorResponse<T> response(ErrorCode errorCode) {
        return new MessageErrorResponse<>(false, errorCode, null);
    }
}