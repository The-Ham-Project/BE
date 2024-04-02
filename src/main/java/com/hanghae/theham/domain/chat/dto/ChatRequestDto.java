package com.hanghae.theham.domain.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

public class ChatRequestDto {

    @Getter
    public static class ChatRoomCreateRequestDto{
        @NotBlank(message = "채팅을 시작할 상대의 닉네임이 있어야 합니다.")
        @Schema(description = "채팅할 상대의 닉네임", example = "카카오라미")
        private String nickname;
    }

}
