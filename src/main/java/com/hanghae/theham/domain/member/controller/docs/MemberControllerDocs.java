package com.hanghae.theham.domain.member.controller.docs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hanghae.theham.domain.member.dto.MemberRequestDto.MemberUpdatePositionRequestDto;
import com.hanghae.theham.domain.member.dto.MemberResponseDto.GoogleUserInfoDto;
import com.hanghae.theham.domain.member.dto.MemberResponseDto.KakaoUserInfoDto;
import com.hanghae.theham.domain.member.dto.MemberResponseDto.MemberUpdatePositionResponseDto;
import com.hanghae.theham.domain.member.dto.MemberResponseDto.NaverUserInfoDto;
import com.hanghae.theham.global.dto.ResponseDto;
import com.hanghae.theham.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "members", description = "회원 관련 API")
public interface MemberControllerDocs {

    @Operation(summary = "회원 토큰 재발행 기능", description = "회원의 토큰을 재발행할 수 있는 API")
    void reissue(
            HttpServletRequest request,
            HttpServletResponse response
    );

    @Operation(summary = "회원 로그아웃 기능", description = "로그아웃할 수 있는 API")
    void logout(
            HttpServletRequest request,
            HttpServletResponse response
    );

    @Operation(summary = "회원 좌표 갱신 기능", description = "회원 좌표를 갱싱할 수 있는 API")
    ResponseDto<MemberUpdatePositionResponseDto> updatePosition(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            MemberUpdatePositionRequestDto requestDto
    );

    @Operation(summary = "카카오 로그인 기능", description = "카카오 로그인할 수 있는 API")
    ResponseDto<KakaoUserInfoDto> kakaoLogin(
            @RequestParam String code,
            HttpServletResponse response
    ) throws JsonProcessingException;

    @Operation(summary = "구글 로그인 기능", description = "구글 로그인할 수 있는 API")
    ResponseDto<GoogleUserInfoDto> googleLogin(
            @RequestParam String code,
            HttpServletResponse response
    ) throws JsonProcessingException;

    @Operation(summary = "네이버 로그인 기능", description = "네이버 로그인할 수 있는 API")
    ResponseDto<NaverUserInfoDto> naverLogin(
            @RequestParam String code,
            HttpServletResponse response
    ) throws JsonProcessingException;
}
