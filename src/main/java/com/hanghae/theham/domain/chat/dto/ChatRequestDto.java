package com.hanghae.theham.domain.chat.dto;

import com.hanghae.theham.domain.chat.entity.Chat;
import com.hanghae.theham.domain.chat.entity.ChatRoom;
import com.hanghae.theham.domain.member.entity.Member;
import com.hanghae.theham.global.util.BadWordFilteringUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

public class ChatRequestDto {
    @Getter
    public static class ChatSendMessageRequestDto {
        @NotBlank(message = "메세지 내용을 입력해주세요.")
        @Schema(description = "채팅 메세지 내용", example = "안녕하세요? 함께쓰기 문의합니다")
        private String message;

        public Chat toEntity(ChatRoom chatRoom, Member sender, int currentMemberCount) {
            boolean isRead = currentMemberCount == 2;
            // 비속어
            String badWordFilterMessage = BadWordFilteringUtil.change(this.message);
            return Chat.builder()
                    .chatRoom(chatRoom)
                    .sender(sender)
                    .message(badWordFilterMessage)
                    .isRead(isRead)
                    .build();
        }
    }
}
