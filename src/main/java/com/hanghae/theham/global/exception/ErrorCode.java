package com.hanghae.theham.global.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    INTERNAL_SERVER_ERROR("내부 서버 오류가 발생했습니다."),
    NOT_FOUND_MEMBER("찾을 수 없는 회원 정보입니다."),
    MAXIMUM_RENTAL_FILE_UPLOAD("파일은 최대 3개까지 업로드 가능합니다."),
    FILE_SIZE_EXCEEDED("파일 사이즈는 5MB를 초과할 수 없습니다."),
    UNSUPPORTED_FILE_TYPE("지원하지 않는 파일 형식입니다. JPEG와 PNG 이미지만 업로드 가능합니다."),
    ;

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }
}
