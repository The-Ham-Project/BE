package com.hanghae.theham.domain.member.dto;

import com.hanghae.theham.domain.member.entity.Member;
import lombok.Getter;

public class MemberResponseDto {

    @Getter
    public static class MemberReadResponseDto {

        private final String nickname;
        private final String email;
        private final String profileUrl;

        public MemberReadResponseDto(Member member) {
            this.nickname = member.getNickname();
            this.email = member.getEmail();
            this.profileUrl = member.getProfileUrl();
        }
    }

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
    public static class MemberInfoDto {
        private final String id;
        private final String email;
        private final String profileUrl;

        public MemberInfoDto(String id, String email, String profileUrl) {
            this.id = id;
            this.email = email;
            this.profileUrl = profileUrl;
        }
    }
}
