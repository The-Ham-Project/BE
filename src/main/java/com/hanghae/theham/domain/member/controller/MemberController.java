package com.hanghae.theham.domain.member.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hanghae.theham.domain.member.controller.docs.MemberControllerDocs;
import com.hanghae.theham.domain.member.dto.MemberRequestDto.MemberUpdatePositionRequestDto;
import com.hanghae.theham.domain.member.dto.MemberResponseDto.GoogleUserInfoDto;
import com.hanghae.theham.domain.member.dto.MemberResponseDto.KakaoUserInfoDto;
import com.hanghae.theham.domain.member.dto.MemberResponseDto.MemberUpdatePositionResponseDto;
import com.hanghae.theham.domain.member.dto.MemberResponseDto.NaverUserInfoDto;
import com.hanghae.theham.domain.member.service.*;
import com.hanghae.theham.global.dto.ResponseDto;
import com.hanghae.theham.global.security.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/api/v1/members")
@RestController
public class MemberController implements MemberControllerDocs {

    private final AuthService authService;
    private final MemberService memberService;
    private final KakaoService kakaoService;
    private final GoogleService googleService;
    private final NaverService naverService;

    public MemberController(AuthService authService, MemberService memberService, KakaoService kakaoService, GoogleService googleService, NaverService naverService) {
        this.authService = authService;
        this.memberService = memberService;
        this.kakaoService = kakaoService;
        this.googleService = googleService;
        this.naverService = naverService;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/reissue")
    public void reissue(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        authService.reissue(request, response);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/logout")
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        authService.logout(request, response);
    }

    @PatchMapping("/position")
    public ResponseDto<MemberUpdatePositionResponseDto> updatePosition(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody MemberUpdatePositionRequestDto requestDto
    ) {
        MemberUpdatePositionResponseDto responseDto = memberService.updatePosition(userDetails.getUsername(), requestDto);
        return ResponseDto.success("회원 좌표 갱신 기능", responseDto);
    }

    @GetMapping("/kakao/callback")
    public ResponseDto<KakaoUserInfoDto> kakaoLogin(
            @RequestParam String code,
            HttpServletResponse response
    ) throws JsonProcessingException {
        KakaoUserInfoDto responseDto = kakaoService.kakaoLogin(code, response);
        return ResponseDto.success("카카오 로그인 기능", responseDto);
    }

    @GetMapping("/google/callback")
    public ResponseDto<GoogleUserInfoDto> googleLogin(
            @RequestParam String code,
            HttpServletResponse response
    ) throws JsonProcessingException {
        GoogleUserInfoDto responseDto = googleService.googleLogin(code, response);
        return ResponseDto.success("구글 로그인 기능", responseDto);
    }

    @GetMapping("/naver/callback")
    public ResponseDto<NaverUserInfoDto> naverLogin(
            @RequestParam String code,
            HttpServletResponse response
    ) throws JsonProcessingException {
        NaverUserInfoDto responseDto = naverService.naverLogin(code, response);
        return ResponseDto.success("네이버 로그인 기능", responseDto);
    }
}
