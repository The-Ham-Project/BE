package com.hanghae.theham.domain.member.service;

import com.hanghae.theham.domain.member.dto.MemberResponseDto.MemberReadResponseDto;
import com.hanghae.theham.domain.member.entity.Member;
import com.hanghae.theham.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @DisplayName("성공 - 회원 정보 조회")
    @Test
    void getMember_01() {
        // given
        Member member = Member.builder()
                .nickname("더듬이")
                .email("test@test.com")
                .profileUrl("testUrl")
                .build();

        // when
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(member));

        MemberReadResponseDto responseDto = memberService.getMember(member.getEmail());

        // then
        assertEquals("더듬이", responseDto.getNickname());
        assertEquals("test@test.com", responseDto.getEmail());
        assertEquals("testUrl", responseDto.getProfileUrl());
    }
}