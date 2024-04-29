package com.hanghae.theham.domain.notification.dto.type;

import lombok.Getter;

@Getter
public enum NotificationType {
    CONNECTED("sse connected"),
    NEW_CHAT("new chat"),
    READ_CHATROOM_MESSAGE("read chatRoom message"),
    UPDATE_CHATROOM("update chatroom"),
    ;

    private final String message;

    NotificationType(String message) {
        this.message = message;
    }
}