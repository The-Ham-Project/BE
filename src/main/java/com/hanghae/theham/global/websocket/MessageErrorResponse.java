package com.hanghae.theham.global.websocket;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;

@Getter
public class MessageErrorResponse<T> {
    private final int status;
    private final String message;
    @JsonInclude(Include.NON_EMPTY)
    private final T data;

    public MessageErrorResponse(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static <T> MessageErrorResponse<T> response(int status, String message, T data) {
        return new MessageErrorResponse<>(status, message, data);
    }

    public static <T> MessageErrorResponse<T> response(int status, String message) {
        return new MessageErrorResponse<>(status, message, null);
    }
}

