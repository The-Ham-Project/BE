package com.hanghae.theham.domain.member.strategy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hanghae.theham.domain.member.dto.MemberResponseDto.MemberInfoDto;
import com.hanghae.theham.domain.member.entity.Member;
import jakarta.servlet.http.HttpServletResponse;

public interface SocialLoginStrategy {

    MemberInfoDto socialLogin(String code, HttpServletResponse response) throws JsonProcessingException;

    String getToken(String code) throws JsonProcessingException;

    MemberInfoDto getMemberInfo(String token) throws JsonProcessingException;

    Member registerMemberIfNeeded(MemberInfoDto memberInfoDto);
}
