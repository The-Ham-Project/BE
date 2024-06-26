package com.hanghae.theham.domain.member.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hanghae.theham.domain.member.controller.docs.MemberControllerDocs;
import com.hanghae.theham.domain.member.dto.MemberRequestDto.MemberUpdatePositionRequestDto;
import com.hanghae.theham.domain.member.dto.MemberRequestDto.MemberUpdateRequestDto;
import com.hanghae.theham.domain.member.dto.MemberResponseDto.*;
import com.hanghae.theham.domain.member.service.AuthService;
import com.hanghae.theham.domain.member.service.MemberService;
import com.hanghae.theham.domain.member.service.SocialLoginService;
import com.hanghae.theham.domain.rental.dto.RentalResponseDto.RentalMyReadResponseDto;
import com.hanghae.theham.global.dto.ResponseDto;
import com.hanghae.theham.global.security.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public void updateProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestPart @Valid MemberUpdateRequestDto requestDto,
            @RequestPart(required = false) MultipartFile profileImage
    ) {
        memberService.updateProfile(userDetails.getUsername(), requestDto, profileImage);
    }

    @GetMapping("/posts/likes")
    public ResponseDto<List<RentalMyReadResponseDto>> readRentalLikeList(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "6", required = false) int size
    ) {
        List<RentalMyReadResponseDto> responseDtoList = memberService.readRentalLikeList(userDetails.getUsername(), page, size);
        return ResponseDto.success("함께쓰기 내가 좋아요 누른 게시글 조회 기능", responseDtoList);
    }

    @GetMapping("/check-position")
    public ResponseDto<MemberCheckPositionResponseDto> checkPosition(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        MemberCheckPositionResponseDto responseDto = memberService.checkPosition(userDetails.getUsername());
        return ResponseDto.success("회원 좌표 검사 기능", responseDto);
    }

    @GetMapping("/check-nickname/{nickname}")
    public ResponseDto<MemberCheckNicknameResponseDto> checkNickname(
            @PathVariable String nickname
    ) {
        MemberCheckNicknameResponseDto responseDto = memberService.checkNickname(nickname);
        return ResponseDto.success("회원 닉네임 중복 검사 기능", responseDto);
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
