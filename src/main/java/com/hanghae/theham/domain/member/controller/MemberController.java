package com.hanghae.theham.domain.member.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hanghae.theham.domain.member.controller.docs.MemberControllerDocs;
import com.hanghae.theham.domain.member.dto.MemberRequestDto.MemberUpdatePositionRequestDto;
import com.hanghae.theham.domain.member.dto.MemberResponseDto.MemberInfoDto;
import com.hanghae.theham.domain.member.dto.MemberResponseDto.MemberReadResponseDto;
import com.hanghae.theham.domain.member.dto.MemberResponseDto.MemberUpdatePositionResponseDto;
import com.hanghae.theham.domain.member.service.AuthService;
import com.hanghae.theham.domain.member.service.MemberService;
import com.hanghae.theham.domain.member.service.SocialLoginService;
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
    private final SocialLoginService socialLoginService;

    public MemberController(AuthService authService, MemberService memberService, SocialLoginService socialLoginService) {
        this.authService = authService;
        this.memberService = memberService;
        this.socialLoginService = socialLoginService;
    }

    @GetMapping
    public ResponseDto<MemberReadResponseDto> getMember(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        MemberReadResponseDto responseDto = memberService.getMember(userDetails.getUsername());
        return ResponseDto.success("회원 정보 조회 기능", responseDto);
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
    public ResponseDto<MemberInfoDto> kakaoLogin(
            @RequestParam String code,
            HttpServletResponse response
    ) throws JsonProcessingException {
        MemberInfoDto responseDto = socialLoginService.login("kakao", code, response);
        return ResponseDto.success("카카오 로그인 기능", responseDto);
    }

    @GetMapping("/google/callback")
    public ResponseDto<MemberInfoDto> googleLogin(
            @RequestParam String code,
            HttpServletResponse response
    ) throws JsonProcessingException {
        MemberInfoDto responseDto = socialLoginService.login("google", code, response);
        return ResponseDto.success("구글 로그인 기능", responseDto);
    }

    @GetMapping("/naver/callback")
    public ResponseDto<MemberInfoDto> naverLogin(
            @RequestParam String code,
            HttpServletResponse response
    ) throws JsonProcessingException {
        MemberInfoDto responseDto = socialLoginService.login("naver", code, response);
        return ResponseDto.success("네이버 로그인 기능", responseDto);
    }
}
