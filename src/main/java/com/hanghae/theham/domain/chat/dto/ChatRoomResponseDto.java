package com.hanghae.theham.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hanghae.theham.domain.chat.entity.ChatRoom;
import lombok.Getter;

import java.time.LocalDateTime;

public class ChatRoomResponseDto {

    @Getter
    public static class ChatRoomCreateResponseDto {
        private final Long id;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private final LocalDateTime createdAt;

        public ChatRoomCreateResponseDto(ChatRoom chatRoom) {
            this.id = chatRoom.getId();
            this.createdAt = chatRoom.getCreatedAt();
        }
    }

}
