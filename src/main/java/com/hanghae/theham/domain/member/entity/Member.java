package com.hanghae.theham.domain.member.entity;

import com.hanghae.theham.domain.member.entity.type.RoleType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "member_tbl")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String nickname;

    @Column
    private String profileUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleType role = RoleType.ROLE_USER;

    @Column
    private double latitude; // 위도

    @Column
    private double longitude; // 경도

    @Column
    private String kakaoId;

    @Column
    private String googleId;

    @Column
    private String naverId;

    @Column
    private LocalDateTime lastLoginAt;

    @Builder
    public Member(String email, String password, String nickname, String profileUrl, RoleType role, double latitude, double longitude, String kakaoId, String googleId, String naverId, LocalDateTime lastLoginAt) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileUrl = profileUrl;
        this.role = role;
        this.latitude = latitude;
        this.longitude = longitude;
        this.kakaoId = kakaoId;
        this.googleId = googleId;
        this.naverId = naverId;
        this.lastLoginAt = lastLoginAt;
    }

    public Member kakaoIdUpdate(String kakaoId) {
        this.kakaoId = kakaoId;
        return this;
    }

    public Member googleIdUpdate(String googleId) {
        this.googleId = googleId;
        return this;
    }

    public Member naverIdUpdate(String naverId) {
        this.naverId = naverId;
        return this;
    }

    public void updatePosition(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void updateLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public void updateProfile(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }
}
