package com.hanghae.theham.global.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    INTERNAL_SERVER_ERROR("내부 서버 오류가 발생했습니다."),
    NOT_FOUND_MEMBER("찾을 수 없는 회원 정보입니다."),
    MAXIMUM_RENTAL_FILE_UPLOAD("파일은 최대 3개까지 업로드 가능합니다."),
    FILE_SIZE_EXCEEDED("파일 사이즈는 5MB를 초과할 수 없습니다."),
    UNSUPPORTED_FILE_TYPE("지원하지 않는 파일 형식입니다. JPEG와 PNG 이미지만 업로드 가능합니다."),
    NOT_FOUND_RENTAL("찾을 수 없는 함께쓰기 게시글 정보입니다."),
    UNMATCHED_RENTAL_MEMBER("게시글 작성자 정보가 일치하지 않습니다."),
    NOT_FOUND_REFRESH_TOKEN("리프레쉬 토큰을 찾을 수 없습니다."),
    INVALID_REFRESH_TOKEN("사용할 수 없는 리프레쉬 토큰입니다."),
    EXPIRED_ACCESS_TOKEN("액세스 토큰이 만료되었습니다."),
    INVALID_ACCESS_TOKEN("사용할 수 없는 액세스 토큰입니다."),

    // chat
    CHAT_ROOM_ALREADY_EXISTS("채팅방이 이미 존재합니다."),
    CANNOT_CHAT_WITH_SELF("채팅방을 생성할 수 없습니다."),
    NOT_FOUND_CHAT_ROOM("찾을 수 없는 채팅방 입니다."),

    ;

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }
}
