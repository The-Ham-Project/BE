package com.hanghae.theham.domain.member.dto;

import com.hanghae.theham.domain.member.entity.Member;
import lombok.Getter;

public class MemberResponseDto {

    @Getter
    public static class MemberUpdatePositionResponseDto {
        private final Long id;
        private final double latitude;
        private final double longitude;

        public MemberUpdatePositionResponseDto(Member member) {
            this.id = member.getId();
            this.latitude = member.getLatitude();
            this.longitude = member.getLongitude();
        }
    }

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

    @Getter
    public static class NaverUserInfoDto {
        private final String id;
        private final String nickname;
        private final String email;
        private final String profileUrl;

        public NaverUserInfoDto(String id, String nickname, String email, String profileUrl) {
            this.id = id;
            this.nickname = nickname;
            this.email = email;
            this.profileUrl = profileUrl;
        }
    }

    @Getter
    public static class GoogleUserInfoDto {
        private final String id;
        private final String nickname;
        private final String email;
        private final String profileUrl;

        public GoogleUserInfoDto(String id, String nickname, String email, String profileUrl) {
            this.id = id;
            this.nickname = nickname;
            this.email = email;
            this.profileUrl = profileUrl;
        }
    }
}
