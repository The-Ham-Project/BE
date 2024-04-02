package com.hanghae.theham.domain.member.service;

import com.hanghae.theham.domain.member.dto.MemberRequestDto.MemberUpdatePositionRequestDto;
import com.hanghae.theham.domain.member.dto.MemberResponseDto.MemberUpdatePositionResponseDto;
import com.hanghae.theham.domain.member.entity.Member;
import com.hanghae.theham.domain.member.repository.MemberRepository;
import com.hanghae.theham.global.exception.BadRequestException;
import com.hanghae.theham.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(readOnly = true)
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public MemberUpdatePositionResponseDto updatePosition(String email, MemberUpdatePositionRequestDto requestDto) {
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> {
            log.error("회원 정보를 찾을 수 없습니다. 이메일: {}", email);
            return new BadRequestException(ErrorCode.NOT_FOUND_MEMBER.getMessage());
        });

        member.updatePosition(requestDto.getLatitude(), requestDto.getLongitude());
        return new MemberUpdatePositionResponseDto(member);
    }
}
