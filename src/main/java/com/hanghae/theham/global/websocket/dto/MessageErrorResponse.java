package com.hanghae.theham.global.websocket.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;

@Getter
public class MessageErrorResponse<T> {
    private final boolean status;
    private final int code;
    private final String message;
    @JsonInclude(Include.NON_EMPTY)
    private final T data;

    public MessageErrorResponse(boolean status, int code, String message, T data) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> MessageErrorResponse<T> response(int code, String message, T data) {
        return new MessageErrorResponse<>(false, code, message, data);
    }

    public static <T> MessageErrorResponse<T> response(int code, String message) {
        return new MessageErrorResponse<>(false, code, message, null);
    }
}

