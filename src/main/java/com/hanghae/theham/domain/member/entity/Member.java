package com.hanghae.theham.domain.member.entity;

import com.hanghae.theham.domain.member.entity.type.RoleType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private Long kakaoId;

    @Column
    private String googleId;

    @Builder
    public Member(String email, String password, String nickname, String profileUrl, RoleType role, Long kakaoId, String googleId) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileUrl = profileUrl;
        this.role = role;
        this.kakaoId = kakaoId;
        this.googleId = googleId;
    }

    public Member kakaoIdUpdate(Long kakaoId) {
        this.kakaoId = kakaoId;
        return this;
    }

    public Member googleIdUpdate(String googleId) {
        this.googleId = googleId;
        return this;
    }
}
