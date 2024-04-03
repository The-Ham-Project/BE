package com.hanghae.theham.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hanghae.theham.domain.chat.entity.ChatRoom;
import lombok.Getter;

import java.time.LocalDateTime;

public class ChatRoomResponseDto {

    @Getter
    public static class ChatRoomCreateResponseDto {
        private final Long id;
        private String seller;
        private String buyer;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private final LocalDateTime createdAt;

        public ChatRoomCreateResponseDto(ChatRoom chatRoom) {
            this.id = chatRoom.getId();
            this.seller = chatRoom.getSeller().getNickname();
            this.buyer = chatRoom.getBuyer().getNickname();
            this.createdAt = chatRoom.getCreatedAt();
        }
    }
}
