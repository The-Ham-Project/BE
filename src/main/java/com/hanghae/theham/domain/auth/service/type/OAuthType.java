package com.hanghae.theham.domain.auth.service.type;

import lombok.Getter;

@Getter
public enum OAuthType {
    GOOGLE("google"),
    KAKAO("kakao"),
    NAVER("naver");

    private final String value;

    OAuthType(String value) {
        this.value = value;
    }
}
