package com.hanghae.theham.domain.member.controller.docs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hanghae.theham.domain.member.dto.MemberRequestDto.MemberUpdatePositionRequestDto;
import com.hanghae.theham.domain.member.dto.MemberRequestDto.MemberUpdateRequestDto;
import com.hanghae.theham.domain.member.dto.MemberResponseDto.*;
import com.hanghae.theham.domain.rental.dto.RentalResponseDto;
import com.hanghae.theham.global.dto.ResponseDto;
import com.hanghae.theham.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "members", description = "회원 관련 API")
public interface MemberControllerDocs {

    @Operation(summary = "회원 정보 조회 기능", description = "회원의 정보를 조회할 수 있는 API")
    ResponseDto<MemberReadResponseDto> getMember(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    );

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

    @Operation(summary = "회원 정보 수정 기능", description = "회원 정보를 수정할 수 있는 API")
    void updateProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestPart @Valid MemberUpdateRequestDto requestDto,
            @RequestPart(required = false) MultipartFile profileImage
    );

    @Operation(summary = "회원 좌표 갱신 기능", description = "회원 좌표를 갱신할 수 있는 API")
    ResponseDto<MemberUpdatePositionResponseDto> updatePosition(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            MemberUpdatePositionRequestDto requestDto
    );

    @Operation(summary = "좋아요 누른 게시글 조회 기능", description = "내가 좋아요 누른 게시글을 조회할 수 있는 API")
    ResponseDto<List<RentalResponseDto.RentalMyReadResponseDto>> readRentalLikeList(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "6", required = false) int size
    );

    @Operation(summary = "회원 좌표 검사 기능", description = "회원 좌표를 검사할 수 있는 API")
    ResponseDto<MemberCheckPositionResponseDto> checkPosition(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    );

    @Operation(summary = "회원 닉네임 중복 검사 기능", description = "회원 닉네임을 중복 검사할 수 있는 API")
    ResponseDto<MemberCheckNicknameResponseDto> checkNickname(
            @PathVariable String nickname
    );

    @Operation(summary = "카카오 로그인 기능", description = "카카오 로그인할 수 있는 API")
    ResponseDto<MemberInfoDto> kakaoLogin(
            @RequestParam String code,
            HttpServletResponse response
    ) throws JsonProcessingException;

    @Operation(summary = "구글 로그인 기능", description = "구글 로그인할 수 있는 API")
    ResponseDto<MemberInfoDto> googleLogin(
            @RequestParam String code,
            HttpServletResponse response
    ) throws JsonProcessingException;

    @Operation(summary = "네이버 로그인 기능", description = "네이버 로그인할 수 있는 API")
    ResponseDto<MemberInfoDto> naverLogin(
            @RequestParam String code,
            HttpServletResponse response
    ) throws JsonProcessingException;
}
