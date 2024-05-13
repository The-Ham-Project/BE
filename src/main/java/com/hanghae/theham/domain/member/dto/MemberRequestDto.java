package com.hanghae.theham.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

public class MemberRequestDto {

    @Getter
    public static class MemberUpdateRequestDto {

        @Schema(description = "닉네임을 입력해주세요.", example = "더함이")
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
