package com.hanghae.theham.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

public class MemberRequestDto {

    @Getter
    public static class MemberUpdateRequestDto {

        @NotBlank(message = "닉네임을 입력해주세요.")
        private String nickname;
    }

    @Getter
    public static class MemberUpdatePositionRequestDto {

        @Schema(description = "위도", example = "37.541")
        private double latitude;

        @Schema(description = "경도", example = "126.986")
        private double longitude;
    }
}
