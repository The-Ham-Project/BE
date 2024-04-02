package com.hanghae.theham.domain.member.repository;

import com.hanghae.theham.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    Optional<Member> findByKakaoId(Long kakaoId);

    Optional<Member> findByGoogleId(String googleId);

    Optional<Member> findByNaverId(String naverId);

    Optional<Member> findByNickname(String nickname);
}
