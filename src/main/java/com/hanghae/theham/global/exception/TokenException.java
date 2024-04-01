package com.hanghae.theham.global.exception;

import lombok.Getter;

@Getter
public class TokenException extends RuntimeException {

    public TokenException(String message) {
        super(message);
    }
}
