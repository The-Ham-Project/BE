package com.hanghae.theham.global.websocket.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class WebSocketException extends RuntimeException {
    private final HttpStatus status;

    public WebSocketException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
