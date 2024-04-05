package com.hanghae.theham.domain.chat.dto;

import com.hanghae.theham.domain.chat.entity.Chat;
import com.hanghae.theham.domain.chat.entity.ChatRoom;
import com.hanghae.theham.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public class ChatRequestDto {

    @Getter
    public static class ChatSendMessageRequestDto {
        @NotNull(message = "채탱 보내는 멤버의 정보를 입력해주세요.")
        @Schema(description = "채팅 보내는 멤버 Id", example = "3")
        private Long senderId;
        @NotNull(message = "메세지 내용을 입력해주세요.")
        @Schema(description = "채팅 메세지 내용", example = "안녕하세요? 함께쓰기 문의합니다")
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
