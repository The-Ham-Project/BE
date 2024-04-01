package com.hanghae.theham.domain.member.dto;

import lombok.Getter;

public class MemberResponseDto {

    @Getter
    public static class KakaoUserInfoDto {
        private final Long id;
        private final String nickname;
        private final String email;
        private final String profileUrl;

        public KakaoUserInfoDto(Long id, String nickname, String email, String profileUrl) {
            this.id = id;
            this.nickname = nickname;
            this.email = email;
            this.profileUrl = profileUrl;
        }
    }
}
