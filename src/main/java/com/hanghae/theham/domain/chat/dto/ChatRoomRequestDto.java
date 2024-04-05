package com.hanghae.theham.domain.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public class ChatRoomRequestDto {

    @Getter
    public static class ChatRoomCreateRequestDto {
        @NotBlank(message = "채팅을 시작할 상대의 닉네임이 있어야 합니다.")
        @Schema(description = "채팅할 상대의 닉네임", example = "156ebc3d-dc5b-46cb-b6f5-013fbdcff010")
        private String sellerNickname;

        @NotNull(message = "게시글 아이디가 있어야합니다.")
        @Schema(description = "게시글 아이디", example = "3")
        private Long rentalId;
    }
}
