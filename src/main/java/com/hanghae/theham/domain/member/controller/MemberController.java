package com.hanghae.theham.domain.member.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hanghae.theham.domain.member.dto.MemberResponseDto.GoogleUserInfoDto;
import com.hanghae.theham.domain.member.dto.MemberResponseDto.KakaoUserInfoDto;
import com.hanghae.theham.domain.member.service.AuthService;
import com.hanghae.theham.domain.member.service.GoogleService;
import com.hanghae.theham.domain.member.service.KakaoService;
import com.hanghae.theham.global.dto.ResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/api/v1/members")
@RestController
public class MemberController {

    private final AuthService authService;
    private final KakaoService kakaoService;
    private final GoogleService googleService;

    public MemberController(AuthService authService, KakaoService kakaoService, GoogleService googleService) {
        this.authService = authService;
        this.kakaoService = kakaoService;
        this.googleService = googleService;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/reissue")
    public void reissue(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        authService.reissue(request, response);
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
}
