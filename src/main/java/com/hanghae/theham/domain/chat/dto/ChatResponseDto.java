package com.hanghae.theham.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hanghae.theham.domain.chat.entity.Chat;
import lombok.Getter;

import java.time.LocalDateTime;

public class ChatResponseDto {

    @Getter
    public static class ChatReadResponseDto{ //메세지 반환
        private Long roomId;
        private String sender;
        private String message;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime createdAt;

        public ChatReadResponseDto(Chat chat) {
            this.roomId = chat.getId();
            this.message = chat.getMessage();
            this.sender = chat.getSender().getNickname();
            this.createdAt = chat.getCreatedAt();
        }
    }
}
