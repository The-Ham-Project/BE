package com.hanghae.theham.domain.member.repository;

import com.hanghae.theham.domain.member.entity.Member;
import com.hanghae.theham.domain.member.entity.type.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.yml")
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        Member kakao = Member.builder()
                .email("kakao@test.com")
                .password("1234")
                .nickname("더함이001")
                .profileUrl("url")
                .role(RoleType.ROLE_USER)
                .latitude(12.123)
                .longitude(12.123)
                .kakaoId("kakao")
                .build();

        Member naver = Member.builder()
                .email("naver@test.com")
                .password("1234")
                .nickname("더함이002")
                .profileUrl("url")
                .role(RoleType.ROLE_USER)
                .latitude(12.123)
                .longitude(12.123)
                .naverId("naver")
                .build();

        Member google = Member.builder()
                .email("google@test.com")
                .password("1234")
                .nickname("더함이003")
                .profileUrl("url")
                .role(RoleType.ROLE_USER)
                .latitude(12.123)
                .longitude(12.123)
                .googleId("google")
                .build();

        memberRepository.save(kakao);
        memberRepository.save(naver);
        memberRepository.save(google);
    }

    @DisplayName("성공 - 이메일로 회원 찾기 기능")
    @Test
    void findByEmail_01() {
        Optional<Member> member = memberRepository.findByEmail("kakao@test.com");
        assertThat(member).isPresent();
        assertThat(member.get().getEmail()).isEqualTo("kakao@test.com");
    }

    @DisplayName("성공 - 닉네임으로 회원 찾기 기능")
    @Test
    void findByNickname_01() {
        Optional<Member> member = memberRepository.findByNickname("더함이001");
        assertThat(member).isPresent();
        assertThat(member.get().getEmail()).isEqualTo("kakao@test.com");
    }

    @DisplayName("성공 - 카카오ID로 회원 찾기 기능")
    @Test
    void findByKakaoId_01() {
        Optional<Member> member = memberRepository.findByKakaoId("kakao");
        assertThat(member).isPresent();
        assertThat(member.get().getEmail()).isEqualTo("kakao@test.com");
    }

    @DisplayName("성공 - 네이버ID로 회원 찾기 기능")
    @Test
    void findByNaverId_01() {
        Optional<Member> member = memberRepository.findByNaverId("naver");
        assertThat(member).isPresent();
        assertThat(member.get().getEmail()).isEqualTo("naver@test.com");
    }

    @DisplayName("성공 - 구글ID로 회원 찾기 기능")
    @Test
    void findByGoogleId_01() {
        Optional<Member> member = memberRepository.findByGoogleId("google");
        assertThat(member).isPresent();
        assertThat(member.get().getEmail()).isEqualTo("google@test.com");
    }

    @DisplayName("성공 - 닉네임 Max Sequence 값 구하기")
    @Test
    void findMaxNicknameSequence_01() {
        Integer maxSequence = memberRepository.findMaxNicknameSequence();
        assertThat(maxSequence).isEqualTo(3);
    }
}