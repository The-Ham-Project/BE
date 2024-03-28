package com.hanghae.theham.global.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    INTERNAL_SERVER_ERROR("내부 서버 오류가 발생했습니다."),
    ;

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }
}
