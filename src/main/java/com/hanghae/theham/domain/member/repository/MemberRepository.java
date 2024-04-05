package com.hanghae.theham.domain.member.repository;

import com.hanghae.theham.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    Optional<Member> findByKakaoId(String kakaoId);

    Optional<Member> findByGoogleId(String googleId);

    Optional<Member> findByNaverId(String naverId);

    Optional<Member> findByNickname(String nickname);

    @Query("SELECT MAX(CAST(SUBSTRING(m.nickname, 4) AS int)) FROM Member m WHERE m.nickname LIKE '더함이%'")
    Integer findMaxNicknameSequence();
}
