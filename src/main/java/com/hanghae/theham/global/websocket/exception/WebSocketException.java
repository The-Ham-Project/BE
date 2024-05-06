package com.hanghae.theham.global.websocket.exception;

import com.hanghae.theham.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class WebSocketException extends RuntimeException {
    private final ErrorCode code;

    public WebSocketException(ErrorCode code) {
        super(code.getMessage());
        this.code = code;
    }
}
