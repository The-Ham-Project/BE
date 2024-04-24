package com.hanghae.theham.domain.member.service;

import com.hanghae.theham.domain.member.dto.MemberRequestDto.MemberUpdatePositionRequestDto;
import com.hanghae.theham.domain.member.dto.MemberResponseDto.MemberCheckPositionResponseDto;
import com.hanghae.theham.domain.member.dto.MemberResponseDto.MemberReadResponseDto;
import com.hanghae.theham.domain.member.dto.MemberResponseDto.MemberUpdatePositionResponseDto;
import com.hanghae.theham.domain.member.entity.Member;
import com.hanghae.theham.domain.member.repository.MemberRepository;
import com.hanghae.theham.global.exception.BadRequestException;
import com.hanghae.theham.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
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

    @Cacheable(value = "Members", key = "#email", cacheManager = "redisCacheManager")
    public MemberReadResponseDto getMember(String email) {
        Member member = validateMember(email);
        return new MemberReadResponseDto(member);
    }

    @Transactional
    public MemberUpdatePositionResponseDto updatePosition(String email, MemberUpdatePositionRequestDto requestDto) {
        Member member = validateMember(email);

        member.updatePosition(requestDto.getLatitude(), requestDto.getLongitude());
        return new MemberUpdatePositionResponseDto(member);
    }

    public MemberCheckPositionResponseDto checkPosition(String email) {
        Member member = validateMember(email);

        if (member.getLongitude() == 0.0 || member.getLatitude() == 0.0) {
            return new MemberCheckPositionResponseDto(false);
        }
        return new MemberCheckPositionResponseDto(true);
    }

    private Member validateMember(String email) {
        return memberRepository.findByEmail(email).orElseThrow(() -> {
            log.error("회원 정보를 찾을 수 없습니다. 이메일: {}", email);
            return new BadRequestException(ErrorCode.NOT_FOUND_MEMBER.getMessage());
        });
    }
}
