package com.hanghae.theham.domain.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public class ChatRoomRequestDto {
    @Getter
    public static class ChatRoomCreateRequestDto {
        @NotNull(message = "게시글 아이디가 있어야합니다.")
        @Schema(description = "게시글 아이디", example = "3")
        private Long rentalId;
    }
}
