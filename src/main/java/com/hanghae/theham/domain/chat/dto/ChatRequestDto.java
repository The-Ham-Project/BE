package com.hanghae.theham.domain.chat.dto;

import com.hanghae.theham.domain.chat.entity.Chat;
import com.hanghae.theham.domain.chat.entity.ChatRoom;
import com.hanghae.theham.domain.member.entity.Member;
import lombok.Getter;

public class ChatRequestDto {

    @Getter
    public static class ChatSendMessageRequestDto {
        private Long senderId;
        private String message;

        public Chat toEntity(ChatRoom chatRoom, Member sender) {
            return Chat.builder()
                    .chatRoom(chatRoom)
                    .sender(sender)
                    .message(message)
                    .build();
        }
    }
}
