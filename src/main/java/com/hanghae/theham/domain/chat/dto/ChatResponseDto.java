package com.hanghae.theham.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hanghae.theham.domain.chat.entity.Chat;
import lombok.Getter;

import java.time.LocalDateTime;

public class ChatResponseDto {

    @Getter
    public static class ChatReadResponseDto { //메세지 반환
        private final Long chatId;
        private final String sender;
        private final String message;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private final LocalDateTime createdAt;

        public ChatReadResponseDto(Chat chat) {
            this.chatId = chat.getId();
            this.message = chat.getMessage();
            this.sender = chat.getSender().getNickname();
            this.createdAt = chat.getCreatedAt();
        }
    }
}
