package com.hanghae.theham.domain.chat.entity.type;

public enum VisibleType {
    BOTH("두 사람 모두 읽을 수 있음"),
    ONLY_SENDER("보낸 사람만 읽을 수 있음"),
    ONLY_RECEIVER("받는 사람만 읽을 수 있음"),
    NOBODY("모두 읽을 수 없음"),
    ;

    VisibleType(String value) {
    }
}