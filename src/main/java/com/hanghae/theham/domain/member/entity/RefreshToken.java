package com.hanghae.theham.domain.member.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@RedisHash(value = "refresh_token", timeToLive = 60 * 60 * 24 * 7)
public class RefreshToken {

    @Id
    private String token;

    private String email;

    @Builder
    public RefreshToken(String token, String email) {
        this.token = token;
        this.email = email;
    }
}
