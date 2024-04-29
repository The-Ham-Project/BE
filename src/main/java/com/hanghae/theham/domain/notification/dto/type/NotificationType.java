package com.hanghae.theham.domain.notification.dto.type;

import lombok.Getter;

@Getter
public enum NotificationType {
    CONNECTED("SSE 연결성공"),
    CHAT("새로운 메세지"),
    UPDATE_CHATROOM("채팅방 업데이트"),
    ;

    private final String message;

    NotificationType(String message) {
        this.message = message;
    }
}