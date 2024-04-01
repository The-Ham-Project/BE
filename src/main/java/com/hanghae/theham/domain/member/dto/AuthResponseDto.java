package com.hanghae.theham.domain.member.dto;

import lombok.Getter;

public class AuthResponseDto {

    @Getter
    public static class KakaoUserInfoDto {
        private final Long id;
        private final String nickname;
        private final String email;

        public KakaoUserInfoDto(Long id, String nickname, String email) {
            this.id = id;
            this.nickname = nickname;
            this.email = email;
        }
    }
}
